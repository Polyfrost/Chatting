@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide

plugins {
    java
    kotlin("jvm") version("2.1.0")
    val dgtVersion = "2.51.0"
    id("dev.deftu.gradle.tools") version(dgtVersion) // Applies several configurations to things such as the Java version, project name/version, etc.
    id("dev.deftu.gradle.tools.resources") version(dgtVersion) // Applies resource processing so that we can replace tokens, such as our mod name/version, in our resources.
    id("dev.deftu.gradle.tools.bloom") version(dgtVersion) // Applies the Bloom plugin, which allows us to replace tokens in our source files, such as being able to use `@MOD_VERSION` in our source files.
    id("dev.deftu.gradle.tools.shadow") version(dgtVersion) // Applies the Shadow plugin, which allows us to shade our dependencies into our mod JAR. This is NOT recommended for Fabric mods, but we have an *additional* configuration for those!
    id("dev.deftu.gradle.tools.minecraft.loom") version(dgtVersion) // Applies the Loom plugin, which automagically configures Essential's Architectury Loom plugin for you.
    id("dev.deftu.gradle.tools.minecraft.releases") version(dgtVersion) // Applies the Minecraft auto-releasing plugin, which allows you to automatically release your mod to CurseForge and Modrinth.
}

//dependencies {
//    compileOnly("dev.deftu:omnicore-1.21.5-fabric:0.34.0")
//}

toolkitLoomHelper {
    useOneConfig {
        version = "1.0.0-alpha.140"
        loaderVersion = "1.1.0-alpha.49"

        for (module in arrayOf("commands", "config", "config-impl", "events", "internal", "ui", "utils")) {
            +module
        }
    }

    useDevAuth("1.2.1")
    useMixinExtras("0.4.1")

    // Turns off the server-side run configs, as we're building a client-sided mod.
    disableRunConfigs(GameSide.SERVER)

    // Defines the name of the Mixin refmap, which is used to map the Mixin classes to the obfuscated Minecraft classes.
    useMixinRefMap(modData.id)
}
