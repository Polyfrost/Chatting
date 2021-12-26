package com.raeids.stratus.gui.components

import com.raeids.stratus.Stratus
import com.raeids.stratus.mixin.GuiNewChatAccessor
import gg.essential.api.utils.GuiUtil
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class ScreenshotButton :
    CleanButton(448318, { UResolution.scaledWidth - 28 }, { UResolution.scaledHeight - 27 }, 12, 12, "") {

    override fun onMousePress() {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI
        if (GuiUtil.getOpenedScreen() is GuiChat) {
            Stratus.screenshotChat((chat as GuiNewChatAccessor).scrollPos)
        }
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            if (hovered) {
                GlStateManager.color(1f, 1f, 160f / 255f)
            } else {
                GlStateManager.color(1f, 1f, 1f)
            }
            mc.textureManager.bindTexture(ResourceLocation(Stratus.ID, "screenshot.png"))
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }
}