
plugins {
    id("org.jetbrains.kotlin.js")
}

dependencies {
    implementation(project(":model"))
    implementation(project(":configuration"))
    implementation(project(":client"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
    implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
}

kotlin.target.browser {}
