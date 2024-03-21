plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "dev.kobalt"
version = "0000.00.00.00.00.00.000"

repositories {
    //  mavenCentral()
}

fun ktor(module: String, version: String) = "io.ktor:ktor-$module:$version"
fun exposed(module: String, version: String) = "org.jetbrains.exposed:exposed-$module:$version"
fun general(module: String, version: String) = "$module:$version"
fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
fun kotlinw(module: String, version: String) = "org.jetbrains.kotlin-wrappers:kotlin-$module:$version"

fun DependencyHandler.httpServer() {
    implementation(ktor("server-core", "1.6.7"))
    implementation(ktor("server-cio", "1.6.7"))
    implementation(ktor("html-builder", "1.6.7"))
}

fun DependencyHandler.commandLineInterface() {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
}

fun DependencyHandler.standardLibrary() {
    implementation(kotlin("stdlib", "1.6.10"))
}

fun DependencyHandler.logger() {
    implementation(general("org.slf4j:slf4j-simple", "1.7.36"))
}

fun DependencyHandler.markdown() {
    implementation(general("com.vladsch.flexmark:flexmark", "0.62.2"))
}

fun DependencyHandler.htmlParser() {
    implementation(general("org.jsoup:jsoup", "1.14.3"))
}

fun DependencyHandler.htmlDsl() {
    implementation(kotlinx("html-jvm", "0.7.3"))
}

fun DependencyHandler.cssDsl() {
    implementation(kotlinw("css-jvm", "1.0.0-pre.298-kotlin-1.6.10"))
}

dependencies {
    standardLibrary()
    httpServer()
    commandLineInterface()
    markdown()
    htmlParser()
    htmlDsl()
    cssDsl()
    logger()
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("server.jar")
        mergeServiceFiles()
        manifest {
            attributes("Main-Class" to "dev.kobalt.holdem.web.MainKt")
        }
    }
}