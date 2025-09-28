plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.5-fabric"(1_21_05, "yarn") {
        "1.8.9-fabric"(1_08_09, "yarn"){
            "1.8.9-forge"(1_08_09, "srg")
        }
    }
    strictExtraMappings.set(true)
}