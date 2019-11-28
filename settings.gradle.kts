enableFeaturePreview("GRADLE_METADATA")

include(
    ":model",
    ":configuration",
    ":persistence",
    ":client",
    ":client:js",
    ":server",
    ":server:jvm"
)

pluginManagement {
    val detekt_version: String by settings
    val kotlin_version: String by settings

    plugins {
        id("org.jetbrains.kotlin.multiplatform") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
        id("io.gitlab.arturbosch.detekt") version "1.2.0"
    }
}
