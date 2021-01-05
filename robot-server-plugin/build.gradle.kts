import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    id("org.jetbrains.intellij")
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}
val robotServerVersion = rootProject.ext["rr_main_version"] as String + "." + rootProject.ext["rr_build"] as String

version = robotServerVersion

repositories {
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://repo.labs.intellij.net/intellij")
}

dependencies {
    val ktor_version: String by project

    implementation(project(":remote-robot"))

    implementation("io.ktor:ktor-server-core:$ktor_version") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("io.ktor:ktor-server-netty:$ktor_version") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("io.ktor:ktor-gson:$ktor_version") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.assertj:assertj-swing-junit:3.17.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.2.0")

    implementation("org.mozilla:rhino:1.7.12")
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

configure<IntelliJPluginExtension> {
//    setPlugins("org.jetbrains.kotlin:1.4.10-release-IJ2020.2-1")
    updateSinceUntilBuild = false
}

tasks.getByName<RunIdeTask>("runIde") {
    setJbrVersion("11_0_2b159")
    systemProperty("robot-server.port", 8080)
    systemProperty("robot.encryption.enabled", "true")
    systemProperty("robot.encryption.password", "my super secret")
}

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    setChangeNotes(
        """
      api for robot inside Idea<br>
      <em>idea testing</em>"""
    )
}

publishing {
    publications {
        register("publishToBintray", MavenPublication::class) {
            artifact("build/distributions/robot-server-plugin-$robotServerVersion.zip")
            // fix artifact id after gradle-plugin changes
            groupId = "org.jetbrains.test"
            artifactId = "robot-server-plugin"
            version = robotServerVersion
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")

    publish = true

    pkg.apply {
        repo = "intellij-third-party-dependencies"
        name = "robot-server-plugin"
        userOrg = "jetbrains"

        version.apply {
            name = rootProject.ext["rr_main_version"] as String
        }
    }
    setPublications("publishToBintray")
}

publishing {
    publications {
        register("publishToJBMaven", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["rr_version"] as String

            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}


artifactory {
    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications", "publishToJBMaven")
        })
    })
}
