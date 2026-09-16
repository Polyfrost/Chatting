package org.polyfrost.chatting.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.FontRenderer
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.mixin.GuiNewChatAccessor

/**
 * Optional Forge integrations are intentionally neutral on Fabric (Ornithe).
 * Keeping these hooks local preserves the renderer's v2 call sites without
 * introducing hard dependencies on BetterChat or Patcher.
 */
object ModCompatHooks {
    private val minecraft: Minecraft get() = Minecraft.getMinecraft()

    @JvmStatic val xOffset get() = 0
    @JvmStatic val yOffset get() = 0
    @JvmStatic val chatPosition get() = 0
    @JvmStatic val betterChatSmoothMessages get() = false
    @JvmStatic val extendedChatLength get() = 0
    @JvmStatic val fontRenderer: FontRenderer get() = minecraft.fontRendererObj
    @JvmStatic val chatLines: MutableList<ChatLine>
        get() = (minecraft.ingameGUI.chatGUI as GuiNewChatAccessor).chatLines
    @JvmStatic val drawnChatLines: MutableList<ChatLine>
        get() = (minecraft.ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines
    @JvmStatic val chatHeadOffset get() = if (ChattingConfig.showChatHeads) 10 else 0
    @JvmStatic val chatButtonOffset get() =
        (if (ChattingConfig.chatCopy) 10 else 0) + (if (ChattingConfig.chatDelete) 10 else 0)
    @JvmStatic val chatInputLimit get() = 100
    @JvmStatic val shouldDrawInputBox get() = true

    @JvmStatic
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int, chatLine: ChatLine): Int =
        fontRenderer.drawString(text, x + if (ChattingConfig.showChatHeads) 10f else 0f, y, color, ChattingConfig.textRenderType != 0)
}
