package org.polyfrost.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.config.ChattingConfig.chatButtonColor
import org.polyfrost.chatting.config.ChattingConfig.chatButtonHoveredColor

class ClearButton :
    CleanButton(13379014, { if (ChattingConfig.chatSearch) UResolution.scaledWidth - 28 else UResolution.scaledWidth - 14 }, 12, 12, "",
        { RenderType.NONE }) {

    var times = 0

    override fun onMousePress() {
        ++times
        if (times > 1) {
            times = 0
            mc.ingameGUI.chatGUI.clearChatMessages()
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
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "delete.png"))
            val color = if (hovered) chatButtonHoveredColor else chatButtonColor
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, color.getAlpha() / 255f)
                Gui.drawModalRectWithCustomSizedTexture(xPosition + 2, yPosition + 2, 0f, 0f, 10, 10, 10f, 10f)
            }
            GlStateManager.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
            GlStateManager.disableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }
    }
}