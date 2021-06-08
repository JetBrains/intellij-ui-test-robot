plugins {
    id("org.jetbrains.intellij")
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.bouncycastle:bcprov-jdk15on:1.68")

    api("org.assertj:assertj-swing-junit:3.9.2")
    api("org.apache.logging.log4j:log4j-api:2.11.1")
    api("org.apache.logging.log4j:log4j-core:2.11.1")

    implementation("commons-io:commons-io:2.6") {
        isForce = true
    }
    api("com.squareup.retrofit2:retrofit:2.7.1")
    api("com.squareup.retrofit2:converter-gson:2.7.1")
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

intellij {
    version = "LATEST-EAP-SNAPSHOT"
}

publishing {
    publications {
        register("remoteRobot", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}