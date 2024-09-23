plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

allprojects {
    group = "me.cheesetaschisch.blackjack"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.google.devtools.ksp")

    dependencies {
        compileOnly(rootProject.libs.bundles.koin)
        ksp(rootProject.libs.koin.ksp)
    }

    kotlin {
        jvmToolchain(21)
    }
}