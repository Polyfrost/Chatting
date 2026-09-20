plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2" /* [SC] DO NOT EDIT */

stonecutter handlers {
    inherit("aw", "ct")
}

stonecutter parameters {
    replacements.string {
        direction = eval(current.version, "< 1.21.11")
        from = "net.minecraft.Util"
        to = "net.minecraft.util.Util"
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        from = "classTweaker v2 named"
        to = "classTweaker v2 official"
    }
}

stonecutter tasks {
    order("publishModrinth")
}
