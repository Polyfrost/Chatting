package org.polyfrost.chatting.utils

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.getAlpha
import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import com.llamalad7.betterchat.BetterChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.Chatting.isBetterChat
import org.polyfrost.chatting.Chatting.isPatcher
import org.polyfrost.chatting.config.ChattingConfig.offsetNonPlayerMessages
import org.polyfrost.chatting.config.ChattingConfig.showChatHeads
import org.polyfrost.chatting.config.ChattingConfig.showTimestamp
import org.polyfrost.chatting.config.ChattingConfig.textRenderType
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.hook.GuiNewChatHook
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

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
    val betterChatSmoothMessages
        get() = if (isBetterChat) BetterChat.getSettings().smooth else false

    @JvmStatic
    val extendedChatLength
        get() = if (isPatcher) 32667 else 0

    @JvmStatic
    val fontRenderer: FontRenderer
        get() = Minecraft.getMinecraft().fontRendererObj

    @JvmStatic
    val chatLines: List<ChatLine>
        get() = (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor).chatLines

    @JvmStatic
    val drawnChatLines: List<ChatLine>
        get() = (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines

    @JvmStatic
    fun getFullMessage(chatLine: ChatLine): ChatLine? {
        return (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).getFullMessage(chatLine)
    }

    private var hoveredText: ChatLine? = null
    private var hoverProgress = 0L
    private val formatter = SimpleDateFormat("HH:mm")

    fun getTimeStampText(comp: ChatLine): String {
        comp as ChatLineHook
        return "[${formatter.format(Date(comp.timestamp))}] "
    }

    @JvmStatic
    fun setHoveredText(comp: ChatLine?) {
        val newComp = comp?.let(::getFullMessage)
        if (hoveredText != newComp) {
            hoveredText = newComp
            hoverProgress = System.currentTimeMillis()
        }
    }

    @JvmStatic
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int, chatLine: ChatLine, screenshot: Boolean): Int {
        var actualX = x
        val hook = chatLine as ChatLineHook
        if (showTimestamp && !screenshot) {
            val timeOffsetInfo = getTimeOffset(chatLine)
            if (timeOffsetInfo != null) {
                if (timeOffsetInfo.shouldRender) {
                    fontRenderer.drawString(timeOffsetInfo.text, actualX, y, -1, true)
                }
                actualX += timeOffsetInfo.offset
            }
        }
        if (showChatHeads && !screenshot) {
            val renderX = actualX
            if (hook.isDetected || offsetNonPlayerMessages) {
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
                    (renderX).toInt(),
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
                    (renderX).toInt(),
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


    data class TimeOffsetInfo(
        val text: String,
        val shouldRender: Boolean,
        val offset: Int,
    )

    private fun ease(x: Double): Double {
        return if (x < 0.5) 4 * x * x * x else 1 - (-2.0 * x + 2).pow(3.0) / 2
    }

    private fun getTimeOffset(comp: ChatLine): TimeOffsetInfo? {
        if (!showTimestamp) return null
        val root = getFullMessage(comp)
        if (root == null || root != hoveredText) return null
        if ((root as ChatLineHook).children.firstOrNull() != comp) return null
        val text = getTimeStampText(root)
        val strWidth = fontRenderer.getStringWidth(text)
        val animationTime = ((System.currentTimeMillis() - hoverProgress) / 100.0).coerceIn(0.0, 1.0)
        val easedAnimationPercentage = ease(animationTime)
        val progress = (easedAnimationPercentage * strWidth).toInt()
        return TimeOffsetInfo(text, progress == strWidth, progress)
    }

    @JvmStatic
    fun getStartOffset(value: Int, comp: ChatLine): Int {
        var actualX = value
        actualX += getTimeOffset(comp)?.offset ?: 0
        return actualX
    }
}
