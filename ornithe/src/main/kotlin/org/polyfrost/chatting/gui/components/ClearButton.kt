package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.config.ChattingConfig

class ClearButton : CleanButton(
    13379014,
    { ScaledResolution(Minecraft.getMinecraft()).scaledWidth - if (ChattingConfig.chatSearch) 28 else 14 },
    12, 12, "", { RenderType.NONE },
) {
    private var confirmationExpiresAt = 0L

    override fun onMousePress() {
        val minecraft = Minecraft.getMinecraft()
        if (System.currentTimeMillis() <= confirmationExpiresAt) {
            confirmationExpiresAt = 0L
            minecraft.ingameGUI.chatGUI.clearChatMessages()
        } else {
            confirmationExpiresAt = System.currentTimeMillis() + 3000L
            minecraft.thePlayer?.addChatMessage(ChatComponentText("§c§lClick again to clear the chat!"))
        }
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) drawCenteredString(mc.fontRendererObj, "×", xPosition + width / 2, yPosition + 2, if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb)
    }
}
