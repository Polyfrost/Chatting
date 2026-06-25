import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.kikugie.loom-back-compat")
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("dev.deftu.gradle.bloom") version "0.2.0"
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val modid = property("mod.id") as String
val modname = property("mod.name") as String
val modversion = property("mod.version") as String
val moddescription = property("mod.description") as String
val mcversion = stonecutter.current.version
val oneconfigversion = property("oneconfig_version") as String
val loaderversion = property("loader_version") as String

val isModern = stonecutter.eval(mcversion, ">=26.1")
val hasOfficialMappings = !isModern
val javaVersion = if (isModern) 25 else 21

val accessWidener = when {
    stonecutter.eval(mcversion, ">=26.2") -> "chatting-26.2.accesswidener"
    isModern -> "chatting-26.accesswidener"
    stonecutter.eval(mcversion, ">=1.21.11") -> "chatting-1.21.11.accesswidener"
    stonecutter.eval(mcversion, ">=1.21.6") -> "chatting-1.21.6.accesswidener"
    stonecutter.eval(mcversion, ">=1.21.5") -> "chatting-1.21.5.accesswidener"
    else -> "chatting.accesswidener"
}
val allAccessWideners = listOf(
    "chatting.accesswidener", "chatting-1.21.5.accesswidener", "chatting-1.21.6.accesswidener",
    "chatting-1.21.11.accesswidener", "chatting-26.accesswidener", "chatting-26.2.accesswidener"
)

base {
    archivesName.set("$modid-$modversion+$mcversion")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()

    maven("https://maven.parchmentmc.org")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://maven.gegy.dev/releases")

    maven("https://nexus.prsm.wtf/repository/maven-public/maven-repo/releases/")
    maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://maven.deftu.dev/releases")

    maven("https://maven.fabricmc.net/releases")
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        content { includeGroup("net.kyori") }
    }
    maven("https://jitpack.io") {
        content { includeGroupAndSubgroups("com.github") }
    }
    maven("https://maven.bawnorton.com/releases") {
        content { includeGroup("com.github.bawnorton.mixinsquared") }
    }
    maven("https://maven.azureaaron.net/releases") {
        content { includeGroup("net.azureaaron") }
    }
    maven("https://redirector.kotlinlang.org/maven/compose-dev")
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run" // Shared run directory across all Minecraft versions.
    }

    runConfigs.remove(runConfigs["server"]) // Removes server run configs.

    accessWidenerPath.set(rootProject.file("src/main/resources/$accessWidener"))
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    if (hasOfficialMappings) {
        @Suppress("UnstableApiUsage")
        mappings(loom.layered {
            officialMojangMappings()
        })
    }

    modImplementation("net.fabricmc:fabric-loader:$loaderversion")

    modImplementation("org.polyfrost.oneconfig:$mcversion-fabric:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:commands:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:config:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:config-impl:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:events:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:internal:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:ui:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:utils:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:hud:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:notifications:$oneconfigversion")
}

bloom {
    replacement("@MOD_ID@", modid)
    replacement("@MOD_NAME@", modname)
    replacement("@MOD_VERSION@", modversion)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to modid,
        "mod_name" to modname,
        "mod_version" to modversion,
        "mod_description" to moddescription,
        "mc_version" to mcversion,
        "loader_version" to loaderversion,
        "aw" to accessWidener
    )

    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }

    // Bundle only the selected widener
    allAccessWideners.filter { it != accessWidener }.forEach { exclude(it) }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)

    from("LICENSE") {
        rename { "${it}_${inputs.properties["archivesName"]}" }
    }
}

val modrinthMinecraftVersionOverride = mapOf(
    "26.1" to listOf("26.1", "26.1.1", "26.1.2")
)

val modrinthId = listOf("oneconfig.publish.modrinth", "publish.modrinth").firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }
val modrinthToken = listOf("oneconfig.publish.modrinth.token", "publish.modrinth.token", "modrinth.token").firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }
val minecraftVersion = modrinthMinecraftVersionOverride[mcversion] ?: listOf(mcversion)
val publishJarTaskName = if ("remapJar" in tasks.names) "remapJar" else "jar"
val changelogs = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

publishMods {
    file = tasks.named<AbstractArchiveTask>(publishJarTaskName).flatMap { it.archiveFile }

    displayName = modversion
    version = "v$modversion"
    changelog = changelogs
    type = BETA

    modLoaders.add("fabric")

    dryRun = modrinthId == null || modrinthToken == null

    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken.orEmpty()

            minecraftVersions.addAll(minecraftVersion)

            requires("oneconfig")
            requires("fabric-language-kotlin")
        }
    }
}
