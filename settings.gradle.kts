enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(":model")
include(":map")
include(":map-generator")
include(":common")
include(":common-client")
include(":common-server")
include(":server")
