group = "com.chainguard"
version = "0.1.0"

plugins {
    kotlin("jvm") version "1.9.22"
    id("java-gradle-plugin")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.gradle:gradle-test-kit:7.6.4")
}

gradlePlugin {
    plugins {
        create("chainctl-gradle-plugin") {
            id = "com.chainguard.chainctl"
            implementationClass = "com.chainguard.chainctl.ChainctlGradlePlugin"
            displayName = "Chainctl Gradle Plugin"
            description = "Verifies resolved JAR dependencies against Chainguard Libraries using chainctl before fat JAR assembly"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnit()
}
