plugins {
    id("org.jetbrains.intellij")
    id("idea")
}

intellij {
    version.set("LATEST-EAP-SNAPSHOT")
}

repositories {
    mavenCentral()
    jcenter()
}

idea {
    module {
        isDownloadSources = true
    }
}

dependencies {
    implementation(project(":remote-robot"))
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
