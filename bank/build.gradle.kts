plugins {
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(project(":menu"))

    implementation(libs.kotlinx.serialization.json)
}