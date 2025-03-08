package org.polyfrost.chatting.gui.components

import dev.deftu.omnicore.client.OmniScreen
import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class ScreenshotButton :
    CleanButton(
        448318, {
            if (ChattingConfig.chatSearch && ChattingConfig.chatDeleteHistory) OmniResolution.scaledWidth - 42 else if (ChattingConfig.chatSearch || ChattingConfig.chatDeleteHistory) OmniResolution.scaledWidth - 28 else OmniResolution.scaledWidth - 14
        }, 12, 12, "",
        { RenderType.NONE }) {

    override fun onMousePress() {
        if (OmniScreen.currentScreen is GuiChat) {
            Chatting.screenshotChat((mc.ingameGUI.chatGUI as GuiNewChatAccessor).scrollPos)
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
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "screenshot.png"))
            val color = if (hovered) ChattingConfig.chatButtonHoveredColor else ChattingConfig.chatButtonColor
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, color.alpha)
                Gui.drawModalRectWithCustomSizedTexture(xPosition + 2, yPosition + 2, 0f, 0f, 10, 10, 10f, 10f)
            }
            GlStateManager.color(color.r / 255f, color.g / 255f, color.b / 255f, color.alpha)
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
            GlStateManager.popMatrix()
        }
    }
}