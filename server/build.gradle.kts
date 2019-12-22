import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("${rootProject.projectDir}/detekt/config.yml")
    reports {
        html.enabled = true
    }
}

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
    implementation(project(":map"))
    implementation(project(":model"))
    implementation(project(":common"))
    implementation(project(":common-client"))
    implementation(project(":common-server"))
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
}

val taskGetDb by tasks.register<Copy>("getDbFromMapGenerator") {
    val mapGeneratorProject = project(":map-generator")
    //dependsOn(mapGeneratorProject.tasks.getByName("run"))
    from("${mapGeneratorProject.projectDir}/dungeon.db")
    into("$projectDir")
}

val taskGetJs by tasks.register<Copy>("getJsFromClient") {
    val clientJsProject = project(":common-client")
    dependsOn(clientJsProject.tasks.getByName("jsBrowserWebpack"))
    from("${clientJsProject.buildDir}/distributions/${clientJsProject.name}.js")
    into("$buildDir/external-resources/web")
}

tasks.getByName("processResources").dependsOn(taskGetJs, taskGetDb)
