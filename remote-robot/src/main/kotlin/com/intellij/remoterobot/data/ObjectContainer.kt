// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.data

import com.intellij.remoterobot.utils.serializeToBytes
import java.io.File
import java.io.Serializable
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors
import kotlin.concurrent.withLock


class ObjectContainer(
    val className: String,
    val classMap: Map<String, ByteArray>,
    val objectBytes: ByteArray,
    val runInEdt: Boolean
) : Serializable

private val JAR_FS_GUARD = ReentrantLock()

fun Function<*>.pack(runInEdt: Boolean = false): ObjectContainer {
    val klass = javaClass

    if (klass.name.contains("Generated_for_debugger", true)) {
        val classBytes = debuggerClassesDir()?.listFiles()?.firstOrNull {
            it.name.startsWith(klass.name)
        }?.readBytes() ?: throw Exception("ByteArray for class ${klass.name} is null")
        val objectBytes = this.serializeToBytes()
        return ObjectContainer(klass.name, mapOf(klass.canonicalName to classBytes), objectBytes, runInEdt)
    }

    val pathToClass = klass.name.replace(".", "/") + ".class"
    val classFileUri =
        klass.classLoader.getResource(pathToClass)?.toURI() ?: throw IllegalStateException("Cannot find $klass class file")
    val classesMap = if (classFileUri.scheme == "jar") {
        JAR_FS_GUARD.withLock {
            FileSystems.newFileSystem(classFileUri, emptyMap<String, String>()).use { jarFileSystem ->
                readNecessaryClasses(jarFileSystem.getPath(pathToClass).parent, klass)
            }
        }
    } else {
        readNecessaryClasses(Paths.get(classFileUri).parent, klass)
    }

    val objectBytes = this.serializeToBytes()
    return ObjectContainer(klass.name, classesMap, objectBytes, runInEdt)
}

private fun readNecessaryClasses(path: Path, cls: Class<out Function<*>>): Map<String, ByteArray> =
    Files.list(path).use { files ->
        files
            .filter {
                val classNameWithoutPackage = cls.name.substring(cls.name.lastIndexOf(".") + 1)
                it.fileName.toString().startsWith(classNameWithoutPackage)
            }.collect(Collectors.toMap({ classFile ->
                val packageName = cls.`package`?.name?.plus(".") ?: ""
                "$packageName${classFile.fileNameWithoutExtension()}"
            }, { classFile ->
                Files.newInputStream(classFile).use { it.readBytes() }
            }))
    }

private fun debuggerClassesDir(): File? {
    val projectsUrl = ObjectContainer::class.java.classLoader.getResource("references")
        ?: throw IllegalStateException("Cannot find references dir in resources")
    var t = File(projectsUrl.toURI())
    while (t.listFiles()?.any { it.name == "debuggerClasses" } != true) t = t.parentFile
    return t.listFiles()?.firstOrNull { it.name == "debuggerClasses" }
}

private fun Path.fileNameWithoutExtension(): String = fileName.toString().substringBeforeLast(".")

