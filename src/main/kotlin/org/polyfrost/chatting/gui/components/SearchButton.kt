package org.polyfrost.chatting.gui.components

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.libs.universal.UResolution
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.ChatSearchingManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.config.ChattingConfig

class SearchButton() :
    CleanButton(3993935, { UResolution.scaledWidth - 14 }, 12, 12, "",
        { RenderType.NONE }) {
    val inputField = SearchTextField()
    private var chatBox = false

    override fun isEnabled(): Boolean {
        return chatBox
    }

    override fun onMousePress() {
        chatBox = !chatBox
        inputField.setEnabled(chatBox)
        inputField.isFocused = chatBox
        ChatSearchingManager.lastSearch = ""
        inputField.text = ""
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {

        inputField.drawTextBox()
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "search.png"))
            val color = if (isEnabled()) OneColor(200, 200, 200, 255) else if (hovered) ChattingConfig.chatButtonHoveredColor else ChattingConfig.chatButtonColor
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

    inner class SearchTextField : GuiTextField(
        69420,
        Minecraft.getMinecraft().fontRendererObj,
        UResolution.scaledWidth * 4 / 5 - 60,
        UResolution.scaledHeight - 26,
        UResolution.scaledWidth / 5,
        12
    ) {

        init {
            maxStringLength = 100
            enableBackgroundDrawing = true
            isFocused = false
            text = ""
            setCanLoseFocus(true)
        }

        override fun drawTextBox() {
            if (isEnabled()) {
                if (!isFocused) isFocused = true
                super.drawTextBox()
            }
        }
    }
}
