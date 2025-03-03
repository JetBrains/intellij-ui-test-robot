plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

subprojects {
    apply {
        plugin(JavaPlugin::class.java)
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = "com.intellij.remoterobot"

    kotlin {
        jvmToolchain(11)
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }

    if (name in listOf("remote-robot", "remote-fixtures", "robot-server-plugin", "robot-server-core", "ide-launcher", "test-recorder")) {
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

