import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                implementation(project(":common"))
                implementation(project(":map:"))
                implementation("io.ktor:ktor-client-core:${findProperty("ktor_version")}")
                implementation("io.ktor:ktor-client-websockets:${findProperty("ktor_version")}")
                implementation("org.kodein.di:kodein-di:${findProperty("kodein_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${findProperty("kotlin_serialization_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${findProperty("kotlin_serialization_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${findProperty("kotlin_date_time_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
                implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
                implementation("com.benasher44:uuid:${findProperty("uuid_version")}")
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
                implementation("io.ktor:ktor-websockets:${findProperty("ktor_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
                implementation("org.mapdb:mapdb:${findProperty("mapdb_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:${findProperty("kotlin_date_time_version")}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.ktor:ktor-server-test-host:${findProperty("ktor_version")}")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime-js:${findProperty("kotlin_date_time_version")}")
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
