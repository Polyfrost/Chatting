package com.raeids.stratus.hook

import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat

// This exists because mixin doesn't like dummy classes
object ModCompatHooks {
    @JvmStatic
    val xOffset
        get() = BetterChat.getSettings().xOffset

    @JvmStatic
    val yOffset
        get() = BetterChat.getSettings().yOffset

    @JvmStatic
    val chatPosition
        get() = PatcherConfig.chatPosition

}
