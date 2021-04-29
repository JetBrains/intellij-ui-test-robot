import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("org.jetbrains.intellij") version "0.7.3"
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
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}

configure(listOf(project(":remote-robot"), project(":robot-server-plugin"))) {
    apply {
        plugin("maven-publish")
    }
}

ext {
    set("rr_version", "${get("rr_main_version")}.${get("rr_build")}")
}
ext {
    val publishVersion: String =
        get("rr_main_version") as String + "." + get("rr_build") + if (System.getenv("RUN_NUMBER") != null) {
            "." + System.getenv("RUN_NUMBER")
        } else {
            ""
        }
    set("publish_version", publishVersion)
}
