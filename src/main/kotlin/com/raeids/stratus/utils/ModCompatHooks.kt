package com.raeids.stratus.utils

import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import com.raeids.stratus.Stratus.isBetterChat
import com.raeids.stratus.Stratus.isPatcher

// This exists because mixin doesn't like dummy classes
object ModCompatHooks {
    @JvmStatic
    val xOffset
        get() = if (isBetterChat) BetterChat.getSettings().xOffset else 0

    @JvmStatic
    val yOffset
        get() = if (isBetterChat) BetterChat.getSettings().yOffset else 0

    @JvmStatic
    val chatPosition
        get() = if (isPatcher && PatcherConfig.chatPosition) 12 else 0

    @JvmStatic
    val extendedChatLength
        get() = if (isPatcher) 32667 else 0

}
