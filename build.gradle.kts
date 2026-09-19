plugins {
    kotlin("jvm") version "2.3.20"
    java
    id("net.fabricmc.fabric-loom-remap") version "1.17.+"
    id("ploceus") version "1.17.+"
}

group = "org.polyfrost"
version = "2.0.6+mc1.8.9-ornithe"
base.archivesName.set("Chatting")

repositories {
    mavenCentral()
    google()
    maven("https://maven.cloverclient.com/releases") {
        content { includeGroup("pl.tomgirl") }
    }
    maven("https://maven.legacyfabric.net/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://repo.polyfrost.org/releases")
}

ploceus {
    setIntermediaryGeneration(2)
}

loom {
    mods {
        create("chatting") { sourceSet("main") }
    }
    runs { remove(getByName("server")) }
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings(ploceus.mcpMappings("stable", "1.8.9", "22"))

    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    ploceus.dependOsl("0.20.3")

    // The current Ornithe build is published as a Fabric mod with its API modules
    // nested inside. Keeping this as a mod dependency makes development and the
    // produced metadata agree on the required runtime library.
    modImplementation("org.polyfrost.oneconfig:1.8.9-ornithe:1.2.0")
    modImplementation("com.terraformersmc:modmenu:0.5.0+mc1.8.9")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}
