import groovy.lang.GroovyObject
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins {
    id("com.jfrog.artifactory") version "4.9.9"
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("org.jetbrains.intellij") version "0.7.2"
}

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin(JavaPlugin::class.java)
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = "com.intellij.remoterobot"

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}

configure(listOf(project(":remote-robot"), project(":robot-server-plugin"))) {
    apply {
        plugin("com.jfrog.artifactory")
        plugin("maven-publish")
    }

    artifactory {
        setContextUrl("https://repo.labs.intellij.net")
        publish(delegateClosureOf<PublisherConfig> {
            repository(delegateClosureOf<GroovyObject> {
                setProperty("repoKey", "intellij")
                setProperty("maven", true)
            })
            setPublishPom(true)
        })
    }
}

ext {
    set("rr_version", "${get("rr_main_version")}.${get("rr_build")}")
}
