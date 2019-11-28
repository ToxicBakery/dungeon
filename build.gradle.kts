plugins {
    id("io.gitlab.arturbosch.detekt")
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

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        failFast = true
        buildUponDefaultConfig = true
        config = files("${rootProject.projectDir}/detekt/config.yml")
        input = files(
            "$projectDir/src/commonMain/kotlin"
        )
        reports {
            html.enabled = true
        }
    }
}
