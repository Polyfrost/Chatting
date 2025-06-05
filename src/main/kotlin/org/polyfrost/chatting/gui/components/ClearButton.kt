package org.polyfrost.chatting.gui.components

import dev.deftu.omnicore.client.OmniChat
import dev.deftu.omnicore.client.render.OmniResolution
import dev.deftu.textile.minecraft.MCSimpleTextHolder
import dev.deftu.textile.minecraft.MCTextFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.config.ChattingConfig.chatButtonColor
import org.polyfrost.chatting.config.ChattingConfig.chatButtonHoveredColor
import org.polyfrost.oneconfig.utils.v1.Multithreading
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class ClearButton :
    CleanButton(
        13379014, { if (ChattingConfig.chatSearch) OmniResolution.scaledWidth - 28 else OmniResolution.scaledWidth - 14 }, 12, 12, "",
        { RenderType.NONE }) {

    var times = 0

    override fun onMousePress() {
        ++times
        if (times > 1) {
            times = 0
            mc.ingameGUI.chatGUI.clearChatMessages()
        } else {
            OmniChat.displayClientMessage(MCSimpleTextHolder("Click again to clear the chat!").withFormatting(
                MCTextFormat.RED, MCTextFormat.BOLD))
            Multithreading.submit {
                Thread.sleep(3000)
                times = 0
            }
        }
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            GlStateManager.pushMatrix()
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.blendFunc(770, 771)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "delete.png"))
            val color = if (hovered) chatButtonHoveredColor else chatButtonColor
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, color.alpha)
                drawModalRectWithCustomSizedTexture(xPosition + 2, yPosition + 2, 0f, 0f, 10, 10, 10f, 10f)
            }
            GlStateManager.color(color.r / 255f, color.g / 255f, color.b / 255f, color.alpha)
            drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
            GlStateManager.popMatrix()
        }
    }
}