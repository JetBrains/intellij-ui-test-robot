version = rootProject.ext["publish_version"] as String

plugins {
    id("org.jetbrains.intellij")
}

repositories {
    maven("https://repo.labs.intellij.net/intellij")
}


dependencies {
    api(project(":robot-server-core"))
    api(project(":remote-fixtures"))
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
    version.set("2022.3")
}

publishing {
    publications {
        register("testRecorderJar", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "test-recorder"
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}