plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.5-fabric"(1_21_05, "yarn") {
        "1.21.4-fabric"(1_21_04, "yarn") {
            "1.21.1-fabric"(1_21_01, "yarn") {
                "1.16.5-fabric"(1_16_05, "yarn", file("versions/mappings/1.21.1-fabric+1.16.5-fabric.txt")) {
                    "1.16.5-forge"(1_16_05, "srg") {
                        "1.12.2-forge"(1_12_02, "srg", file("versions/mappings/1.16.5-forge+1.12.2-forge.txt")) {
                            "1.12.2-fabric"(1_12_02, "yarn") {
                                "1.8.9-fabric"(1_08_09, "yarn") {
                                    "1.8.9-forge"(1_08_09, "srg")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    strictExtraMappings.set(true)
}