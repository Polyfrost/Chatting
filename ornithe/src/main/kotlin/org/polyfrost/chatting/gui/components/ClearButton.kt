package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.config.ChattingConfig

class ClearButton : CleanButton(
    13379014,
    { ScaledResolution(Minecraft.getMinecraft()).scaledWidth - if (ChattingConfig.chatSearch) 28 else 14 },
    12, 12, "", { RenderType.NONE },
) {
    override fun onMousePress() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) drawIcon(mc, DELETE, if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb)
    }

    companion object {
        private val DELETE = ResourceLocation("chatting", "delete.png")
    }
}
