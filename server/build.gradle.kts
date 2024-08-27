import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.detekt)
    application
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

sourceSets {
    main {
        resources.srcDir("${layout.buildDirectory.get()}/external-resources")
    }
}

dependencies {
    implementation(projects.map)
    implementation(projects.model)
    implementation(projects.common)
    implementation(projects.commonClient)
    implementation(projects.commonServer)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.websockets)
    implementation(libs.kodein)
    implementation(libs.arbor.jvm)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.protobuf)
    implementation(libs.kotlin.datetime.jvm)
    implementation(libs.logback.classic)
}

val taskGetDb by tasks.register<Copy>("getDbFromMapGenerator") {
    val mapGeneratorProject = project(":map-generator")
    dependsOn(mapGeneratorProject.tasks.getByName("run"))
    from("${mapGeneratorProject.projectDir}/dungeon.db")
    into("$projectDir")
}

val taskGetJs by tasks.register<Copy>("getJsFromClient") {
    val clientJsProject = project(":common-client")
    from("${clientJsProject.layout.buildDirectory.get()}/distributions/${clientJsProject.name}.js")
    into("${layout.buildDirectory.get()}/external-resources/web")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/detekt/config.yml"))
    baseline = file("$projectDir/config/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    dependsOn(taskGetDb)
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true)
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.dependsOn += taskGetDb
tasks.getByName("processResources").dependsOn(taskGetJs, taskGetDb)
tasks.getByName("compileJava").dependsOn(":map-generator:run")
