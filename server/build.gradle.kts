plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    js() {
        browser {
        }
        nodejs {
        }
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
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.UnstableDefault")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            languageSettings.useExperimentalAnnotation("kotlin.Experimental")
        }
        sourceSets["commonMain"].dependencies {
            implementation(kotlin("stdlib-common"))
            implementation("io.ktor:ktor-server-netty:${findProperty("ktor_version")}")
            implementation("io.ktor:ktor-websockets:${findProperty("ktor_version")}")
            implementation("org.kodein.di:kodein-di-erased:${findProperty("kodein_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${findProperty("kotlin_coroutines_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${findProperty("kotlin_serialization_version")}")
            implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
            implementation("com.benasher44:uuid:${findProperty("uuid_version")}")
            implementation("co.touchlab:stately:${findProperty("stately_version")}")

            implementation(project(":configuration"))
            implementation(project(":model"))
            implementation(project(":persistence"))
        }
        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation("io.ktor:ktor-server-test-host:${findProperty("ktor_version")}")
        }
        sourceSets["jvmMain"].apply {
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${findProperty("kotlin_serialization_version")}")
            }
        }
        sourceSets["jsMain"].dependencies {
            implementation(kotlin("test-junit"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${findProperty("kotlin_coroutines_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${findProperty("kotlin_serialization_version")}")
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }
}
