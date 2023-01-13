package cc.woverflow.chatting.utils

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.getAlpha
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.woverflow.chatting.Chatting.isBetterChat
import cc.woverflow.chatting.Chatting.isPatcher
import cc.woverflow.chatting.config.ChattingConfig.offsetNonPlayerMessages
import cc.woverflow.chatting.config.ChattingConfig.showChatHeads
import cc.woverflow.chatting.config.ChattingConfig.textRenderType
import cc.woverflow.chatting.hook.ChatLineHook
import cc.woverflow.chatting.hook.GuiNewChatHook
import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager

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
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int, chatLine: ChatLine, screenshot: Boolean): Int {
        var actualX = x
        if (showChatHeads && !screenshot) {
            val hook = chatLine as ChatLineHook
            if (hook.hasDetected() || offsetNonPlayerMessages) {
                actualX += 10f
            }
            val networkPlayerInfo = hook.playerInfo
            if (networkPlayerInfo != null) {
                GlStateManager.enableBlend()
                GlStateManager.enableAlpha()
                GlStateManager.enableTexture2D()
                mc.textureManager.bindTexture(networkPlayerInfo.locationSkin)
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                GlStateManager.color(1.0f, 1.0f, 1.0f, color.getAlpha() / 255f)
                Gui.drawScaledCustomSizeModalRect(
                    (x).toInt(),
                    (y - 1f).toInt(),
                    8.0f,
                    8.0f,
                    8,
                    8,
                    8,
                    8,
                    64.0f,
                    64.0f
                )
                Gui.drawScaledCustomSizeModalRect(
                    (x).toInt(),
                    (y - 1f).toInt(),
                    40.0f,
                    8.0f,
                    8,
                    8,
                    8,
                    8,
                    64.0f,
                    64.0f
                )
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            }
        }
        return when (textRenderType) {
            0 -> fontRenderer.drawString(text, actualX, y, color, false)
            2 -> TextRenderer.drawBorderedText(text,
                actualX,
                y,
                color,
                (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).textOpacity)
            else -> fontRenderer.drawString(text, actualX, y, color, true)
        }
    }
}
