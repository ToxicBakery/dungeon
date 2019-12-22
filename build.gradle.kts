
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.3.61" apply false
}

allprojects {
    group = "com.toxicbakery.game.dungeon"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("http://dl.bintray.com/kotlin/kotlinx.html/") }
    }
}
