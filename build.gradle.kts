plugins {
    id("java")
}

group = "de.scrupy"
version = "1.0-SNAPSHOT"

allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.1.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }
}