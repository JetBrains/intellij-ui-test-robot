plugins {
    id("org.jetbrains.intellij.platform")
    id("idea")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
}

dependencies {
    implementation(project(":remote-robot"))

    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

idea {
    module {
        isDownloadSources = true
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("remoteFixtures", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}
