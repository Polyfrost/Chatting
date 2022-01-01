package cc.woverflow.chattils.gui.components

import cc.woverflow.chattils.Chattils
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
open class CleanButton(buttonId: Int, private val x: () -> Int, private val y: () -> Int, widthIn: Int, heightIn: Int, name: String) :
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
        xPosition = x.invoke()
        yPosition = y.invoke()
        if (visible) {
            val fontrenderer = mc.fontRendererObj
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            hovered =
                mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
            if (!Chattils.isPatcher || !PatcherConfig.transparentChatInputField) {
                drawRect(
                    xPosition,
                    yPosition,
                    xPosition + width,
                    yPosition + height,
                    if (hovered) hoveredColor else color
                )
            }
            mouseDragged(mc, mouseX, mouseY)
            var j = 14737632
            if (packedFGColour != 0) {
                j = packedFGColour
            } else if (!enabled) {
                j = 10526880
            } else if (hovered) {
                j = 16777120
            }
            drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, j)
        }
    }

    companion object {
        private val hoveredColor = Color(255, 255, 255, 128).rgb
        private val color = Color(0, 0, 0, 128).rgb
    }
}