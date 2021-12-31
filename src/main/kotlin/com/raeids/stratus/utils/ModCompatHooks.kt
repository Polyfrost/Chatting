package com.raeids.stratus.utils

import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import com.raeids.stratus.Stratus.isBetterChat
import com.raeids.stratus.Stratus.isPatcher
import com.raeids.stratus.config.StratusConfig.textRenderType
import com.raeids.stratus.utils.RenderHelper.drawBorderedString
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

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

    @JvmStatic
    val fontRenderer: FontRenderer
        get() = Minecraft.getMinecraft().fontRendererObj

    @JvmStatic
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int): Int {
        return when (textRenderType) {
            0 -> {
                fontRenderer.drawString(text, x, y, color, false)
            }
            2 -> {
                drawBorderedString(fontRenderer, text, x.toInt(), y.toInt(), color)
            }
            else -> fontRenderer.drawString(text, x, y, color, true)
        }
    }
}
