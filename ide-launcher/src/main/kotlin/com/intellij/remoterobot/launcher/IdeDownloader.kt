package com.intellij.remoterobot.launcher

import com.google.gson.JsonParser
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class IdeDownloader @JvmOverloads constructor(private val httpClient: OkHttpClient = OkHttpClient()) {

    private companion object {
        const val ROBOT_PLUGIN_VERSION_DEFAULT = "0.11.9"

        fun getRobotServerPluginDownloadUrl(version: String): String =
            "https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/com/intellij/remoterobot/robot-server-plugin/$version/robot-server-plugin-$version.zip"

        fun getFeedsOsPropertyName() = when (Os.hostOS()) {
            Os.WINDOWS -> "windowsZip"
            Os.LINUX -> "linux"
            Os.MAC -> "mac"
        }
    }

    fun downloadAndExtractLatestEap(ide: Ide, toDir: Path): Path {
        return downloadAndExtract(ide, toDir, Ide.BuildType.EAP)
    }

    @JvmOverloads
    fun downloadAndExtract(
        ide: Ide,
        toDir: Path,
        buildType: Ide.BuildType = Ide.BuildType.EAP,
        version: String? = null,
        buildNumber: String? = null
    ): Path {
        val idePackage = downloadIde(ide, buildType, version, buildNumber, toDir)
        return extractIde(idePackage, toDir)
    }

    @JvmOverloads
    fun downloadRobotPlugin(toDir: Path, version: String = ROBOT_PLUGIN_VERSION_DEFAULT): Path {
        return downloadFile(getRobotServerPluginDownloadUrl(version), toDir.resolve("robot-server-plugin-$version"))
    }

    private fun extractIde(idePackage: Path, toDir: Path): Path = when (Os.hostOS()) {
        Os.LINUX -> extractTar(idePackage, toDir).single()
        Os.MAC -> extractDmgApp(idePackage, toDir)
        Os.WINDOWS -> {
            val appDir =
                Files.createDirectory(toDir.resolve(idePackage.fileName.toString().substringBefore(".win.zip")))
            extractZip(idePackage, appDir)
            appDir
        }
    }

    private fun downloadIde(ide: Ide, buildType: Ide.BuildType, version: String?, buildNumber: String?, toDir: Path): Path {
        val ideDownloadLink = getIdeDownloadUrl(ide, buildType, version, buildNumber)
        val idePackageName = ideDownloadLink.substringAfterLast("/").removeSuffix("/")
        val targetFile = toDir.resolve(idePackageName)
        return downloadFile(ideDownloadLink, targetFile)
    }

    private fun downloadFile(url: String, toFile: Path): Path {
        return httpClient.newCall(Request.Builder().url(url).build()).execute().use { response ->
            check(response.isSuccessful) { "failed to download file from $url" }
            Files.newOutputStream(toFile, StandardOpenOption.CREATE_NEW).use {
                response.body!!.byteStream().buffered().copyTo(it)
            }
            toFile
        }
    }

    private fun getIdeDownloadUrl(ide: Ide, buildType: Ide.BuildType, version: String?, buildNumber: String?): String {
        return httpClient.newCall(
            Request.Builder().url(
                "https://data.services.jetbrains.com/products/releases".toHttpUrl()
                    .newBuilder()
                    .addQueryParameter("code", ide.feedsCode)
                    .addQueryParameter("type", buildType.title)
                    .addQueryParameter("platform", getFeedsOsPropertyName())
                    .build()
            ).build()
        ).execute().use { response ->
            check(response.isSuccessful) { "failed to get $ide feeds" }
            JsonParser.parseReader(response.body!!.charStream())
                .asJsonObject[ide.feedsCode]
                .asJsonArray
                .firstOrNull {
                    val entry = it.asJsonObject
                    (entry["downloads"]?.asJsonObject?.keySet()?.isNotEmpty() ?: false)
                            && (version == null || entry["version"]?.asString == version)
                            && (buildNumber == null || entry["build"]?.asString == buildNumber)
                }
                ?.asJsonObject?.get("downloads")
                ?.asJsonObject?.get(getFeedsOsPropertyName())
                ?.asJsonObject?.get("link")
                ?.asString ?: error("no suitable ide found")
        }
    }
}