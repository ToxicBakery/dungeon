enableFeaturePreview("GRADLE_METADATA")

include(":common")
include(":server")

//include(":model")
//include(":configuration")
//include(":persistence")
//include(":client")
//include(":client:js")
//include(":server:jvm")

pluginManagement {
//    val detekt_version: String by settings
    val kotlin_version: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlin_version
        id("org.jetbrains.kotlin.js") version kotlin_version
        id("org.jetbrains.kotlin.multiplatform") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
//        id("io.gitlab.arturbosch.detekt") version detekt_version
    }
}
