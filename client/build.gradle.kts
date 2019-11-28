plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    js() {
        browser {}
        nodejs {}
    }
    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(project(":configuration"))
            implementation(project(":model"))
            implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
        }
        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
        sourceSets["jsMain"].dependencies {
            implementation(kotlin("stdlib-js"))
            implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
        }
    }
}
