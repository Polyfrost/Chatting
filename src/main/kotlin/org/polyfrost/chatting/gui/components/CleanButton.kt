package org.polyfrost.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.renderer.TextRenderer
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.hook.GuiNewChatHook

/**
 * Taken from ChatShortcuts under MIT License
 * https://github.com/P0keDev/ChatShortcuts/blob/master/LICENSE
 * @author P0keDev
 */
open class CleanButton(
    buttonId: Int,
    private val x: () -> Int,
    widthIn: Int,
    heightIn: Int,
    name: String,
    private val renderType: () -> RenderType,
    private val textColor: (packedFGColour: Int, enabled: Boolean, hovered: Boolean) -> Int = { packedFGColour: Int, enabled: Boolean, hovered: Boolean ->
        var j = 14737632
        if (packedFGColour != 0) {
            j = packedFGColour
        } else if (!enabled) {
            j = 10526880
        } else if (hovered) {
            j = 16777120
        }
        j
    },
) :
    GuiButton(buttonId, x.invoke(), 0, widthIn, heightIn, name) {

    open fun isEnabled(): Boolean {
        return false
    }

    open fun onMousePress() {

    }

    open fun setPositionY() {
        yPosition = UResolution.scaledHeight - 27 + if (ChattingConfig.compactInputBox) 13 else 0
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        val isPressed =
            visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
        if (isPressed) {
            onMousePress()
        }
        return isPressed
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        enabled = isEnabled()
        xPosition = x()
        setPositionY()
        if (visible) {
            val fontrenderer = mc.fontRendererObj
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            hovered =
                mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
            if (!Chatting.isPatcher || !PatcherConfig.transparentChatInputField) {
                drawRect(
                    xPosition,
                    yPosition,
                    xPosition + width,
                    yPosition + height,
                    getBackgroundColor(hovered)
                )
            }
            mouseDragged(mc, mouseX, mouseY)
            val j = textColor(packedFGColour, enabled, hovered)
            when (renderType()) {
                RenderType.NONE, RenderType.SHADOW -> {
                    drawCenteredString(
                        fontrenderer,
                        displayString,
                        xPosition + width / 2,
                        yPosition + (height - 8) / 2,
                        j
                    )
                }

                RenderType.FULL -> {
                    TextRenderer.drawBorderedText(
                        displayString,
                        ((xPosition + width / 2) - (fontrenderer.getStringWidth(displayString) / 2)).toFloat(),
                        (yPosition + (height - 8) / 2).toFloat(),
                        j,
                        (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).`chatting$getTextOpacity`()
                    )
                }
            }
        }
    }

    private fun getBackgroundColor(hovered: Boolean) =
        if (hovered) ChattingConfig.chatButtonHoveredBackgroundColor.rgb
        else ChattingConfig.chatButtonBackgroundColor.rgb
}