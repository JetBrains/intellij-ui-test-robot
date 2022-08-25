import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.1"
}

repositories {
    mavenCentral()
}

intellij {
    version.set("LATEST-EAP-SNAPSHOT")
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

    if (name in listOf("remote-robot", "remote-fixtures", "robot-server-plugin", "robot-server-core", "ide-launcher")) {
        apply {
            plugin("maven-publish")
        }

        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "SpaceInternal"
                    url = uri("https://packages.jetbrains.team/maven/p/iuia/qa-automation-maven")
                    credentials {
                        username = System.getenv("SPACE_INTERNAL_ACTOR")
                        password = System.getenv("SPACE_INTERNAL_TOKEN")
                    }
                }
                maven {
                    name = "SpacePublic"
                    url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
                    credentials {
                        username = System.getenv("SPACE_ACTOR")
                        password = System.getenv("SPACE_TOKEN")
                    }
                }
            }
        }
    }
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

