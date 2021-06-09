import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    id("org.jetbrains.intellij")
}
val robotServerVersion = if (System.getenv("SNAPSHOT") == null) {
    rootProject.ext["publish_version"] as String
} else {
    "0.0.1-SNAPSHOT"
}
version = robotServerVersion

repositories {
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

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.2")

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
        register("robotServerPlugin", MavenPublication::class) {
            artifact("build/distributions/robot-server-plugin-$robotServerVersion.zip")
            groupId = project.group as String
            artifactId = project.name
            version = robotServerVersion
        }
        register("robotServerJar", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "robot-server"
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}