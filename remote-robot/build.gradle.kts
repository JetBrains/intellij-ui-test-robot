import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins {
    `maven-publish`
    id("org.jetbrains.intellij")
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.bouncycastle:bcprov-jdk15on:1.66")

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
    version = "2020.3.2"
}

publishing {
    repositories {
        maven {
            name = "SpaceInternal"
            url = uri("https://packages.jetbrains.team/maven/p/iuia/maven")
            credentials {
                username = System.getenv("SPACE_INTERNAL_ACTOR")
                password = System.getenv("SPACE_INTERNAL_TOKEN")
            }
        }
        maven {
            name = "SpacePublic"
            url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
            credentials {
                username = System.getenv("SPACE_INTERNAL_ACTOR")
                password = System.getenv("SPACE_INTERNAL_TOKEN")
            }
        }
    }
    publications {
        register("remoteRobot", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["rr_main_version"] as String + "." + (System.getenv("RUN_NUMBER")
                ?: rootProject.ext["rr_build"])

            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}