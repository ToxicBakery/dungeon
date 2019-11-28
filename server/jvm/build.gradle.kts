plugins {
    id("kotlin")
    application
}

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

sourceSets {
    main {
        resources.srcDirs("$buildDir/external-resources")
    }
}

dependencies {
    implementation(project(":server"))
    implementation("ch.qos.logback:logback-classic:+")
}

val taskGetJs by tasks.register<Copy>("getJsFromClient") {
    val clientJsProject = project(":client:js")
    dependsOn(clientJsProject.tasks.getByName("browserWebpack"))
    from("${clientJsProject.buildDir}/distributions/js-$version.js")
    into("$buildDir/external-resources/web")
    rename { filename: String -> filename.replace("-$version", "") }
}
val taskGetResources by tasks.register<Copy>("getResourcesFromClient"){
    val clientJsProject = project(":client:js")
    dependsOn(clientJsProject.tasks.getByName("browserWebpack"))
    from("${clientJsProject.buildDir}/processedResources/Js/main")
    into("$buildDir/external-resources/web")
}
tasks.getByName("compileKotlin").dependsOn(taskGetJs, taskGetResources)
