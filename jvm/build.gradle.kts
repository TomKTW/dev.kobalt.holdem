plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "dev.kobalt"
version = "0000.00.00.00.00.00.000"

fun ktor(module: String, version: String) = "io.ktor:ktor-$module:$version"
fun exposed(module: String, version: String) = "org.jetbrains.exposed:exposed-$module:$version"
fun general(module: String, version: String) = "$module:$version"
fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
fun kotlinw(module: String, version: String) = "org.jetbrains.kotlin-wrappers:kotlin-$module:$version"

fun DependencyHandler.httpServer() {
    implementation(ktor("server-core", "1.6.3"))
    implementation(ktor("server-sessions", "1.6.3"))
    implementation(ktor("server-cio", "1.6.3"))
    implementation(ktor("websockets", "1.6.3"))
}

fun DependencyHandler.serialization() {
    implementation(kotlinx("serialization-json", "1.0.0"))
    implementation(kotlinx("serialization-core", "1.0.0"))
}

fun DependencyHandler.standardLibrary() {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation(kotlin("stdlib", "1.5.21"))
}

fun DependencyHandler.logger() {
    implementation(general("ch.qos.logback:logback-classic", "1.2.3"))
}

dependencies {
    standardLibrary()
    httpServer()
    serialization()
    logger()
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("holdem.jvm.jar")
        mergeServiceFiles()
        manifest {
            attributes("Main-Class" to "dev.kobalt.holdem.jvm.MainKt")
        }
    }
}