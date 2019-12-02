plugins {
    id("org.jetbrains.kotlin.js")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-js"))
    implementation(project(":client"))
    implementation(project(":configuration"))
    implementation(project(":model"))
    implementation("com.ToxicBakery.logging:arbor-js:${findProperty("arbor_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${findProperty("kotlin_serialization_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${findProperty("kotlin_serialization_version")}")
}

kotlin {
    target.browser {}
    sourceSets.all {
        languageSettings.useExperimentalAnnotation("kotlinx.serialization.UnstableDefault")
        languageSettings.useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
        languageSettings.useExperimentalAnnotation("kotlin.Experimental")
    }
}
