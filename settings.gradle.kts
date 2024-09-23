rootProject.name = "blackjack"

include(
    fileTree(".")
        .apply {
            include("**/build.gradle.kts")
            exclude("build.gradle.kts")
        }
        .map { it.relativeTo(rootProject.projectDir) }
        .map { it.parent }
        .map { it.replace(File.separator, ":") }
)