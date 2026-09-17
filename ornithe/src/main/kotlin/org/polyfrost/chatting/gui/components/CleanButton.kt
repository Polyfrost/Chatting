package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.config.ChattingConfig

/** Vanilla chat-control base without Forge or OmniCore helpers. */
open class CleanButton(
    buttonId: Int,
    private val x: () -> Int,
    widthIn: Int,
    heightIn: Int,
    name: String,
    private val renderType: () -> RenderType,
    private val textColor: (packedFGColour: Int, enabled: Boolean, hovered: Boolean) -> Int = { packed, enabled, hovered ->
        when {
            packed != 0 -> packed
            !enabled -> 10526880
            hovered -> 16777120
            else -> 14737632
        }
    },
) : GuiButton(buttonId, x(), 0, widthIn, heightIn, name) {
    open fun isEnabled(): Boolean = false

    open fun onMousePress() = Unit

    open fun setPositionY() {
        val scaledHeight = ScaledResolution(Minecraft.getMinecraft()).scaledHeight
        yPosition = scaledHeight - 27
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        val pressed = visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
        if (pressed) onMousePress()
        return pressed
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        enabled = isEnabled()
        xPosition = x()
        setPositionY()
        if (!visible) return
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.blendFunc(770, 771)
        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, backgroundColor(hovered))
        mouseDragged(mc, mouseX, mouseY)
        if (renderType() != RenderType.FULL) {
            drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor(0, enabled, hovered))
        }
    }

    private fun backgroundColor(hovered: Boolean) =
        if (hovered) ChattingConfig.chatButtonHoveredBackgroundColor.argb else ChattingConfig.chatButtonBackgroundColor.argb
}
