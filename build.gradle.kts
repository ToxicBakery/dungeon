plugins {
    kotlin("multiplatform") version libs.versions.kotlin.asProvider() apply false
    kotlin("plugin.serialization") version libs.versions.kotlin.asProvider() apply false
    alias(libs.plugins.detekt) apply false
}

allprojects {
    group = "com.toxicbakery.game.dungeon"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }
}
