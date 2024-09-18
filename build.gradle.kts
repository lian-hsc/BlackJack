plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "me.cheesetastisch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    kotlin {
        jvmToolchain(21)
    }
}