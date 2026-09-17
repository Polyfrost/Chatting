package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.chat.ChatScreenshot
import org.polyfrost.chatting.config.ChattingConfig

/** Captures the currently visible chat lines without relying on Forge helpers. */
class ScreenshotButton : CleanButton(
    448318,
    {
        ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 14 * (
            1 + (if (ChattingConfig.chatSearch) 1 else 0) + (if (ChattingConfig.chatDeleteHistory) 1 else 0)
        )
    },
    12, 12, "", { RenderType.NONE },
) {
    override fun onMousePress() = ChatScreenshot.capture()

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) drawIcon(mc, SCREENSHOT, if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb)
    }

    companion object {
        private val SCREENSHOT = ResourceLocation("chatting", "screenshot.png")
    }
}
