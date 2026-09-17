package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig

class ScreenshotButton : CleanButton(
    448318,
    {
        val precedingButtons = listOf(ChattingConfig.chatSearch, ChattingConfig.chatDeleteHistory).count { it }
        ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 14 * (precedingButtons + 1)
    },
    12, 12, "", { RenderType.NONE },
) {
    override fun onMousePress() {
        Chatting.doTheThing = true
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) drawCenteredString(mc.fontRendererObj, "□", xPosition + width / 2, yPosition + 2, if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb)
    }
}
