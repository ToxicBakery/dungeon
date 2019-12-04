plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenCentral()
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xinline-classes"
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.ToxicBakery.logging:arbor-jvm:${findProperty("arbor_version")}")
    implementation("org.kodein.di:kodein-di-erased-jvm:${findProperty("kodein_version")}")
    implementation("org.mapdb:mapdb:${findProperty("mapdb_version")}")
    testImplementation("junit:junit:4.12")
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("../detekt/config.yml")
    reports {
        html.enabled = true
    }
}
