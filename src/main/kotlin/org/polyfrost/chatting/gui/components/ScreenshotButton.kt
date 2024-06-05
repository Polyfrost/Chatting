package org.polyfrost.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.utils.dsl.mc
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.config.ChattingConfig

class ScreenshotButton :
    CleanButton(448318, {
        if (ChattingConfig.chatSearch && ChattingConfig.chatDeleteHistory) UResolution.scaledWidth - 42 else if (ChattingConfig.chatSearch || ChattingConfig.chatDeleteHistory) UResolution.scaledWidth - 28 else UResolution.scaledWidth - 14
                        }, 12, 12, "",
        { RenderType.NONE }) {

    override fun onMousePress() {
        if (UScreen.currentScreen is GuiChat) {
            Chatting.screenshotChat((mc.ingameGUI.chatGUI as GuiNewChatAccessor).scrollPos)
        }
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "screenshot.png"))
            val color = if (hovered) ChattingConfig.chatButtonHoveredColor else ChattingConfig.chatButtonColor
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, color.alpha / 255f)
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