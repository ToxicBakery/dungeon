plugins {
    id("org.jetbrains.kotlin.js")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":model"))
    implementation(project(":configuration"))
    implementation(project(":client"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:${findProperty("kotlin_html_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${findProperty("kotlin_serialization_version")}")
    implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
}

kotlin {
    target.browser {}
    sourceSets.all {
        languageSettings.useExperimentalAnnotation("kotlinx.serialization.UnstableDefault")
        languageSettings.useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
        languageSettings.useExperimentalAnnotation("kotlin.Experimental")
    }
}
