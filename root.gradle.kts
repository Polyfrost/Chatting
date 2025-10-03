plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.21.5-fabric"(1_21_05, "yarn") {
        "1.21.4-fabric"(1_21_04, "yarn") {
            "1.21.1-fabric"(1_21_01, "yarn") {
                "1.16.5-fabric"(1_16_05, "yarn", file("versions/mappings/1.21.1-fabric+1.16.5-fabric.txt")) {
                    "1.12.2-fabric"(1_12_02, "yarn", file("versions/mappings/1.16.5-fabric+1.12.2-fabric.txt")) {
                        "1.12.2-forge"(1_12_02, "srg") {
                            "1.8.9-forge"(1_08_09, "srg")
                        }
                    }
                }
            }
        }
    }
    strictExtraMappings.set(true)
}