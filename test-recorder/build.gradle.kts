version = rootProject.ext["publish_version"] as String

plugins {
    id("org.jetbrains.intellij.platform")
}

repositories {
    maven("https://repo.labs.intellij.net/intellij")

    intellijPlatform {
        defaultRepositories()
    }
}


dependencies {
    api(project(":robot-server-core"))
    api(project(":remote-fixtures"))

    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

intellijPlatform {
    buildSearchableOptions = false
    pluginConfiguration {
        ideaVersion {
            untilBuild = provider { null }
        }
    }
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