package com.raeids.stratus.hook

import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

/**
 * Taken from ChatShortcuts under MIT License
 * https://github.com/P0keDev/ChatShortcuts/blob/master/LICENSE
 * @author P0keDev
 */
class CleanButton(buttonId: Int, x: Int, y: Int, widthIn: Int, heightIn: Int, private val chatTab: ChatTab) :
    GuiButton(buttonId, x, y, widthIn, heightIn, chatTab.name) {

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        val isPressed =
            enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
        if (isPressed) {
            ChatTabs.currentTab = chatTab
        }
        return isPressed
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        enabled = chatTab != ChatTabs.currentTab
        yPosition = UResolution.scaledHeight - 26
        if (visible) {
            val fontrenderer = mc.fontRendererObj
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            hovered =
                mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
            drawRect(
                xPosition,
                yPosition,
                xPosition + width,
                yPosition + height,
                if (hovered) hoveredColor else color
            )
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