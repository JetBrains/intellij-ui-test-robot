version = rootProject.ext["publish_version"] as String

repositories {
    maven("https://repo.labs.intellij.net/intellij")
}


dependencies {
    api(project(":remote-robot"))

    implementation("org.mozilla:rhino:1.7.14")
    implementation("org.assertj:assertj-swing-junit:3.17.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.8.2")
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
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