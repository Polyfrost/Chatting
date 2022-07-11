package cc.woverflow.chatting.utils

import cc.woverflow.chatting.Chatting.isBetterChat
import cc.woverflow.chatting.Chatting.isPatcher
import cc.woverflow.chatting.config.ChattingConfig.textRenderType
import cc.woverflow.chatting.hook.GuiNewChatHook
import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
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
            0 -> fontRenderer.drawString(text, x, y, color, false)
            2 -> fontRenderer.drawBorderedString(
                text,
                x.toInt(),
                y.toInt(),
                color,
                (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).textOpacity
            )

            else -> fontRenderer.drawString(text, x, y, color, true)
        }
    }


    private val regex = Regex("(?i)\\u00A7[0-9a-f]")
    private var bypassNameHighlight = false
    fun FontRenderer.drawBorderedString(
        text: String, x: Int, y: Int, color: Int, opacity: Int
    ): Int {
        val noColors = text.replace(regex, "\u00A7r")
        var yes = 0
        if (opacity > 3) {
            bypassNameHighlight = true
            for (xOff in -2..2) {
                for (yOff in -2..2) {
                    if (xOff * xOff != yOff * yOff) {
                        yes +=
                            drawString(
                                noColors, (xOff / 2f) + x, (yOff / 2f) + y, (opacity) shl 24, false
                            )

                    }
                }
            }
            bypassNameHighlight = false
        }
        yes +=
                //#if MODERN==0
            drawString(text, x, y, color)

        return yes
    }
}
