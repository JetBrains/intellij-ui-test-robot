version = rootProject.ext["publish_version"] as String

plugins {
    id("org.jetbrains.intellij")
}

repositories {
    maven("https://repo.labs.intellij.net/intellij")
}


dependencies {
    api(project(":remote-robot"))

    implementation("org.mozilla:rhino:1.7.15")
    implementation("org.assertj:assertj-swing:3.17.1")
    implementation("net.bytebuddy:byte-buddy-dep:1.14.15")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

intellij {
    updateSinceUntilBuild.set(false)
    version.set("LATEST-EAP-SNAPSHOT")
}

publishing {
    publications {
        register("robotServerCoreJar", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "robot-server-core"
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}