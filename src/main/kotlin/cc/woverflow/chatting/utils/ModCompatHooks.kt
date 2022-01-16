package cc.woverflow.chatting.utils

import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import cc.woverflow.chatting.Chatting.isBetterChat
import cc.woverflow.chatting.Chatting.isPatcher
import cc.woverflow.chatting.config.ChattingConfig.textRenderType
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
                fontRenderer.drawBorderedString(text, x.toInt(), y.toInt(), color)
            }
            else -> fontRenderer.drawString(text, x, y, color, true)
        }
    }
}
