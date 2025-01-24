dependencies {
    implementation(libs.bundles.koin)

    implementation(project(":terminal"))
    implementation(project(":menu"))
    implementation(project(":rule"))
    implementation(project(":bank"))
    implementation(project(":game"))
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

        manifest {
            attributes["Main-Class"] = "me.blackjack.application.ApplicationKt"
        }
    }
}