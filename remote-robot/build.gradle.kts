import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins {
    `maven-publish`
    id("org.jetbrains.intellij")
    id("com.jfrog.bintray") version "1.8.4"
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.bouncycastle:bcprov-jdk15on:1.66")

    //Logging Network Calls
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")

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
        register("publishToBintray", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "remote-robot"
            version =
                rootProject.ext["rr_main_version"] as String + "." + rootProject.ext["rr_build"] as String
            artifact(sourcesJar)
            pom
        }
    }
}


artifactory {
    publish(delegateClosureOf<PublisherConfig> {
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", "publishToJBMaven")
        })
    })
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")

    publish = true

    pkg.apply {
        repo = "intellij-third-party-dependencies"
        name = "remote-robot"
        userOrg = "jetbrains"

        version.apply {
            name = rootProject.ext["rr_main_version"] as String
        }
    }
    setPublications("publishToBintray")
}