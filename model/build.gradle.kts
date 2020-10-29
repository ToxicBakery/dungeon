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
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
        }
        sourceSets["commonMain"].dependencies {
            implementation(kotlin("stdlib-common"))
            implementation("io.ktor:ktor-client-core:${findProperty("ktor_version")}")
            //implementation("io.ktor:ktor-websockets:${findProperty("ktor_version")}")
            implementation("org.kodein.di:kodein-di-erased:${findProperty("kodein_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${findProperty("kotlin_serialization_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${findProperty("kotlin_serialization_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
            implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
        }
        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
        sourceSets["jvmMain"].apply {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("com.benasher44:uuid:${findProperty("uuid_version")}")
            }
        }
        sourceSets["jvmTest"].dependencies {
            implementation(kotlin("test-junit"))
        }
        sourceSets["jsMain"].dependencies {
            implementation(kotlin("stdlib-js"))
            implementation("org.kodein.di:kodein-di-erased-js:${findProperty("kodein_version")}")
            implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
        }
    }
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("${rootProject.projectDir}/detekt/config.yml")
    input = files(
        kotlin.sourceSets
            .flatMap { sourceSet -> sourceSet.kotlin.srcDirs }
            .map { file -> file.relativeTo(projectDir) }
    )
    reports {
        html.enabled = true
    }
}
