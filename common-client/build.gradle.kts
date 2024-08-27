import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.detekt)
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

kotlin {
    js {
        browser {}
        nodejs {}
    }
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.map)
                implementation(projects.model)
                implementation(projects.common)
                implementation(kotlin("stdlib"))
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)
                implementation(libs.kodein)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.protobuf)
                implementation(libs.kotlin.coroutines)
                implementation(libs.arbor)
                
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
                dependencies {
                    implementation(libs.kotlin.coroutines)
                }
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlin.html.js)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${findProperty("kotlin_coroutines_version")}")
            }
        }
    }
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

