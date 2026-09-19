package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.polyfrost.chatting.chat.Textures
import org.polyfrost.chatting.config.ChattingConfig

class ClearButton : CleanButton(
    13379014,
    {
        ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 14 * (
            1 + (if (ChattingConfig.chatSearch) 1 else 0) + (if (ChattingConfig.chatScreenshot) 1 else 0)
        )
    },
    12, 12, "", { RenderType.NONE },
) {
    override fun onMousePress() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) drawIcon(mc, Textures.DELETE, if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb)
    }

}
