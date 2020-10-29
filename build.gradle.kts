
plugins {
    kotlin("multiplatform") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
}

allprojects {
    group = "com.toxicbakery.game.dungeon"
    version = "1.0.0-SNAPSHOT"

    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://kotlin.bintray.com/kotlinx/") }
    }
}
