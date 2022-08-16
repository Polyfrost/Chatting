package cc.woverflow.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.mixin.GuiNewChatAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class ScreenshotButton :
    CleanButton(448318, { UResolution.scaledWidth - 42 }, { UResolution.scaledHeight - 27 }, 12, 12, "",
        { RenderType.NONE }) {

    override fun onMousePress() {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI
        if (UScreen.currentScreen is GuiChat) {
            Chatting.screenshotChat((chat as GuiNewChatAccessor).scrollPos)
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
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "screenshot.png"))
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }
}