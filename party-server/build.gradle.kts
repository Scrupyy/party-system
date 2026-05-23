plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
}

group = "de.scrupy"
version = "1.0-SNAPSHOT"

repositories {
}

dependencies {
    implementation("io.lettuce:lettuce-core:6.7.1.RELEASE")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(project(":party-core"))
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "de.scrupy.party.server.Main"
        )
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}