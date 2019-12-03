import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
//    id("io.gitlab.arturbosch.detekt")
}

//detekt {
//    failFast = true
//    buildUponDefaultConfig = true
//    config = files("${rootProject.projectDir}/detekt/config.yml")
//    input = files(
//        "$projectDir/src/commonMain/kotlin"
//    )
//    reports {
//        html.enabled = true
//    }
//}

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xinline-classes"
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"

sourceSets {
    main {
        resources.srcDir("$buildDir/external-resources")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-client-core:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-server-netty:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-websockets:${findProperty("ktor_version")}")
    implementation("org.kodein.di:kodein-di-erased-jvm:${findProperty("kodein_version")}")
    implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${findProperty("kotlin_serialization_version")}")
    implementation("ch.qos.logback:logback-classic:+")
    implementation("com.benasher44:uuid:${findProperty("uuid_version")}")
    implementation("co.touchlab:stately:${findProperty("stately_version")}")
}

val taskGetJs by tasks.register<Copy>("getJsFromClient") {
    val clientJsProject = project(":common")
    dependsOn(clientJsProject.tasks.getByName("jsBrowserWebpack"))
    from("${clientJsProject.buildDir}/distributions/common.js")
    into("$buildDir/external-resources/web")
}
//val taskGetResources by tasks.register<Copy>("getResourcesFromClient"){
//    val clientJsProject = project(":client:js")
//    dependsOn(clientJsProject.tasks.getByName("browserWebpack"))
//    from("${clientJsProject.buildDir}/processedResources/Js/main")
//    into("$buildDir/external-resources/web")
//}
tasks.getByName("compileKotlin").dependsOn(taskGetJs)
