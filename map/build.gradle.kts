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
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
        }
    }
    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(project(":model"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${findProperty("kotlin_serialization_version")}")
        implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
        implementation("org.kodein.di:kodein-di-erased:${findProperty("kodein_version")}")
        implementation("com.soywiz.korlibs.klock:klock:${findProperty("klock_version")}")
    }
    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        implementation("io.ktor:ktor-server-test-host:${findProperty("ktor_version")}")
    }
    sourceSets["jvmMain"].dependencies {
        implementation("org.mapdb:mapdb:${findProperty("mapdb_version")}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${findProperty("kotlin_serialization_version")}")
    }
    sourceSets["jsMain"].dependencies {
        implementation(kotlin("stdlib-js"))
        implementation("org.kodein.di:kodein-di-erased-js:${findProperty("kodein_version")}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${findProperty("kotlin_serialization_version")}")
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
