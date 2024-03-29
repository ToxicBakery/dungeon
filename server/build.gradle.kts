import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    application
}

detekt {
    config = files("$rootDir/detekt/config.yml")
    source.from(
        files(
            kotlin.sourceSets
                .flatMap { sourceSet -> sourceSet.kotlin.srcDirs }
                .map { file -> file.relativeTo(projectDir) }
        )
    )
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.kotlinOptions.freeCompilerArgs = listOf(
    "-opt-in=kotlin.time.ExperimentalTime",
    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    "-opt-in=kotlinx.coroutines.FlowPreview",
    "-opt-in=kotlinx.serialization.ImplicitReflectionSerializer",
    "-opt-in=io.ktor.util.KtorExperimentalAPI",
    "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
)

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
    implementation("io.ktor:ktor-server-auth:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-client-core:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-server-call-logging:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-server-default-headers:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-server-netty:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-server-websockets:${findProperty("ktor_version")}")
    implementation("io.ktor:ktor-websockets:${findProperty("ktor_version")}")
    implementation("org.kodein.di:kodein-di:${findProperty("kodein_version")}")
    implementation("com.ToxicBakery.logging:common:${findProperty("arbor_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("kotlin_coroutines_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${findProperty("kotlin_serialization_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${findProperty("kotlin_serialization_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:${findProperty("kotlin_date_time_version")}")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("com.benasher44:uuid:${findProperty("uuid_version")}")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${findProperty("detekt_version")}")
}

val taskGetDb by tasks.register<Copy>("getDbFromMapGenerator") {
    val mapGeneratorProject = project(":map-generator")
    dependsOn(mapGeneratorProject.tasks.getByName("run"))
    from("${mapGeneratorProject.projectDir}/dungeon.db")
    into("$projectDir")
}

val taskGetJs by tasks.register<Copy>("getJsFromClient") {
    val clientJsProject = project(":common-client")
    from("${clientJsProject.buildDir}/distributions/${clientJsProject.name}.js")
    into("$buildDir/external-resources/web")
}

tasks.getByName("processResources").dependsOn(taskGetJs, taskGetDb)
tasks.getByName("compileJava").dependsOn(":map-generator:run")