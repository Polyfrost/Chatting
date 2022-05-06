package cc.woverflow.chatting.gui.components

import cc.woverflow.chatting.Chatting
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class ClearButton :
    CleanButton(13379014, { UResolution.scaledWidth - 28 }, { UResolution.scaledHeight - 27 }, 12, 12, "",
        { RenderType.NONE }) {

    override fun onMousePress() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            if (hovered) GlStateManager.color(1f, 1f, 160f / 255f)
            else GlStateManager.color(1f, 1f, 1f)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "delete.png"))
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }
}