plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig").version("3.0.3")
    id("io.gitlab.arturbosch.detekt")
    application
}

application {
    mainClass.set("com.toxicbakery.game.dungeon.map.MainKt")
}

buildConfig {
    packageName("com.toxicbakery.game.dungeon.map")
    buildConfigField("int", "MAP_SIZE", "32")
    buildConfigField("int", "REGION_SIZE", "4")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs.plus(
            listOf(
                "-Xinline-classes",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlinx.serialization.ImplicitReflectionSerializer",
                "-opt-in=io.ktor.util.KtorExperimentalAPI",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            )
        )
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":model"))
    implementation(project(":map"))
    implementation(project(":common"))
    implementation("com.ToxicBakery.library.noise:generator-jvm:1.0.10")
    implementation("com.ToxicBakery.logging:arbor-jvm:${findProperty("arbor_version")}")
    implementation("org.kodein.di:kodein-di-erased-jvm:${findProperty("kodein_version")}")
    implementation("org.mapdb:mapdb:${findProperty("mapdb_version")}")

    testImplementation("junit:junit:4.12")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${findProperty("detekt_version")}")
}

detekt {
    config = files("$rootDir/detekt/config.yml")
    source.from(
        files(
            kotlin.sourceSets
                .flatMap { sourceSet -> sourceSet.kotlin.srcDirs }
                .map { file -> file.relativeTo(projectDir) }
        )
    )
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
}
