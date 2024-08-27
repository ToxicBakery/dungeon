import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm")
    alias(libs.plugins.build.config)
    alias(libs.plugins.detekt)
    application
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

application {
    mainClass.set("com.toxicbakery.game.dungeon.map.MainKt")
}

val mapSize: String
    get() {
        return if (project.hasProperty("mapSize")) project.property("mapSize").toString()
        else "128"
    }

buildConfig {
    packageName("com.toxicbakery.game.dungeon.map")
    buildConfigField("int", "MAP_SIZE", mapSize)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(projects.model)
    implementation(projects.map)
    implementation(projects.common)
    implementation(libs.noise.generator)
    implementation(libs.arbor.jvm)
    implementation(libs.kodein.jvm)
    implementation(libs.mapdb)

    testImplementation(kotlin("test-junit"))
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/detekt/config.yml"))
    baseline = file("$projectDir/config/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true)
    }
}
