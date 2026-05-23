plugins {
    id("java")
}

group = "de.scrupy"
version = "1.0-SNAPSHOT"

repositories {
}

dependencies {
    implementation("io.lettuce:lettuce-core:6.7.1.RELEASE")
    compileOnly("com.google.code.gson:gson:2.13.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}