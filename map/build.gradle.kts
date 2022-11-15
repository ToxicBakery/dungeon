import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    js {
        browser {}
        nodejs {}
    }
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(project(":model"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${findProperty("kotlin_serialization_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${findProperty("kotlin_serialization_version")}")
                implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
                implementation("org.kodein.di:kodein-di:${findProperty("kodein_version")}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.mapdb:mapdb:${findProperty("mapdb_version")}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${findProperty("detekt_version")}")
}

tasks.withType(KotlinCompile::class.java).configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs.plus(
            listOf(
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
