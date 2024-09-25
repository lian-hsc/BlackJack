plugins {
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(project(":menu"))

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
}