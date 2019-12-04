enableFeaturePreview("GRADLE_METADATA")

include(":map")
include(":map-generator")
include(":common")
include(":common-client")
include(":common-server")
include(":server")

pluginManagement {
    val detekt_version: String by settings
    val kotlin_version: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlin_version
        id("org.jetbrains.kotlin.js") version kotlin_version
        id("org.jetbrains.kotlin.multiplatform") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
        id("io.gitlab.arturbosch.detekt") version detekt_version
    }
}
