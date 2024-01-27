package org.polyfrost.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.Multithreading
import org.polyfrost.chatting.Chatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.config.ChattingConfig

class ClearButton :
    CleanButton(13379014, { if (ChattingConfig.chatSearch) UResolution.scaledWidth - 28 else UResolution.scaledWidth - 14 }, 12, 12, "",
        { RenderType.NONE }) {

    var times = 0

    override fun onMousePress() {
        ++times
        if (times > 1) {
            times = 0
            Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
        } else {
            UChat.chat(ChatColor.RED + ChatColor.BOLD.toString() + "Click again to clear the chat!")
            Multithreading.runAsync {
                Thread.sleep(3000)
                times = 0
            }
        }
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "delete.png"))
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, 1f)
                Gui.drawModalRectWithCustomSizedTexture(xPosition + 2, yPosition + 2, 0f, 0f, 10, 10, 10f, 10f)
            }
            if (hovered) GlStateManager.color(1f, 1f, 160f / 255f)
            else GlStateManager.color(1f, 1f, 1f)
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }
}