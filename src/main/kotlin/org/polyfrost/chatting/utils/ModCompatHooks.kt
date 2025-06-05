package org.polyfrost.chatting.utils

import org.polyfrost.oneconfig.utils.v1.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import dev.deftu.omnicore.client.render.OmniGameRendering
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.Chatting.isBetterChat
import org.polyfrost.chatting.Chatting.isPatcher
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.config.ChattingConfig.offsetNonPlayerMessages
import org.polyfrost.chatting.config.ChattingConfig.showChatHeads
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import org.polyfrost.polyui.color.alpha

// This exists because mixin doesn't like dummy classes
//TODO this is no longer an issue, but its nice for organization... keep or remove?
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
    val betterChatSmoothMessages
        get() = if (isBetterChat) BetterChat.getSettings().smooth else false

    @JvmStatic
    val extendedChatLength
        get() = if (isPatcher) 32667 else 0

    @JvmStatic
    val fontRenderer: FontRenderer
        get() = mc.fontRendererObj

    @JvmStatic
    val chatLines: List<ChatLine>
        get() = (mc.ingameGUI.chatGUI as GuiNewChatAccessor).chatLines

    @JvmStatic
    val drawnChatLines: List<ChatLine>
        get() = (mc.ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines

    @JvmStatic
    val chatHeadOffset
        get() = if (showChatHeads) 10 else 0

    @JvmStatic
    val chatButtonOffset
        get() = (if (ChattingConfig.chatCopy) 10 else 0) + (if (ChattingConfig.chatDelete) 10 else 0)

    @JvmStatic
    val chatInputLimit
        get() = if (isPatcher && PatcherConfig.extendedChatLength) 256 else 100

    @JvmStatic
    val shouldDrawInputBox
        get() = !isPatcher || !PatcherConfig.transparentChatInputField

    @JvmStatic
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int, chatLine: ChatLine): Int {
        var actualX = x
        if (showChatHeads) {
            val hook = chatLine as ChatLineHook
            if (hook.`chatting$hasDetected`() || offsetNonPlayerMessages) {
                actualX += 10f
            }
            val networkPlayerInfo = hook.`chatting$getPlayerInfo`()
            if (networkPlayerInfo != null) {
                GlStateManager.enableBlend()
                GlStateManager.enableAlpha()
                GlStateManager.enableTexture2D()
                mc.textureManager.bindTexture(networkPlayerInfo.locationSkin)
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                GlStateManager.color(1.0f, 1.0f, 1.0f, color.alpha / 255f)
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
        val stack = OmniMatrixStack()
        return when (ChattingConfig.textRenderType) {
            0 -> {
                OmniGameRendering.drawText(stack, text, actualX, y, color, false)
                0 // todo
            }
            1 -> {
                OmniGameRendering.drawText(stack, text, actualX, y, color, true)
                0
            }
//            2 -> TextRenderer.drawBorderedText(text, actualX, y, color, color.alpha / 255f) TODO
            else -> fontRenderer.drawString(text, actualX, y, color, true)
        }
    }
}
