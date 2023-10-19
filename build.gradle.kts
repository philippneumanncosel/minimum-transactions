import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("java")
    kotlin("jvm") version "1.9.20-Beta2"
}

group = "de.klosebrothers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.mockito:mockito-core:5.4.0")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.28")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.jgrapht:jgrapht-io:1.5.2")
    implementation("org.jgrapht:jgrapht-ext:1.5.2")



}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}