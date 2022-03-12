package cc.woverflow.chatting.gui.components

import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.hook.GuiNewChatHook
import cc.woverflow.onecore.utils.drawBorderedString
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

/**
 * Taken from ChatShortcuts under MIT License
 * https://github.com/P0keDev/ChatShortcuts/blob/master/LICENSE
 * @author P0keDev
 */
open class CleanButton(buttonId: Int, private val x: () -> Int, private val y: () -> Int, widthIn: Int, heightIn: Int, name: String, private val renderType: () -> RenderType, private val textColor: (packedFGColour: Int, enabled: Boolean, hovered: Boolean) -> Int = { packedFGColour: Int, enabled: Boolean, hovered: Boolean ->
    var j = 14737632
    if (packedFGColour != 0) {
        j = packedFGColour
    } else if (!enabled) {
        j = 10526880
    } else if (hovered) {
        j = 16777120
    }
    j
}) :
    GuiButton(buttonId, x.invoke(), 0, widthIn, heightIn, name) {

    open fun isEnabled(): Boolean {
        return false
    }

    open fun onMousePress() {

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
        yPosition = y()
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
                    if (hovered) hoveredColor else color
                )
            }
            mouseDragged(mc, mouseX, mouseY)
            val j = textColor(packedFGColour, enabled, hovered)
            when (renderType()) {
                RenderType.NONE, RenderType.SHADOW -> {
                    drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, j)
                }
                RenderType.FULL -> {
                    fontrenderer.drawBorderedString(displayString, (xPosition + width / 2) - (fontrenderer.getStringWidth(displayString) / 2), yPosition + (height - 8) / 2, j, (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).textOpacity)
                }
            }
        }
    }

    companion object {
        val hoveredColor = Color(255, 255, 255, 128).rgb
        val color = Color(0, 0, 0, 128).rgb
    }
}