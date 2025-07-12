@file:Suppress("PropertyName")

import groovy.lang.MissingPropertyException

pluginManagement {
    repositories {
        // Releases
        maven("https://maven.deftu.dev/releases")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://jitpack.io/")

        // Snapshots
        maven("https://maven.deftu.dev/snapshots")
        mavenLocal()

        // Default
        gradlePluginPortal()
        mavenCentral()
    }
}

val projectName: String = extra["mod.name"]?.toString()
    ?: throw MissingPropertyException("mod.name has not been set.")

// Configures the root project Gradle name based on the value in `gradle.properties`
rootProject.name = projectName
