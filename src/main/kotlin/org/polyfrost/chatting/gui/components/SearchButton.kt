package org.polyfrost.chatting.gui.components

import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.ChatSearchingManager
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba

class SearchButton() :
    CleanButton(
        3993935, { OmniResolution.scaledWidth - 14 }, 12, 12, "",
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
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.blendFunc(770, 771)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "search.png"))
            val color = if (isEnabled()) rgba(200, 200, 200, 1f) else if (hovered) ChattingConfig.chatButtonHoveredColor else ChattingConfig.chatButtonColor
            if (ChattingConfig.buttonShadow) {
                GlStateManager.color(0f, 0f, 0f, color.alpha)
                Gui.drawModalRectWithCustomSizedTexture(xPosition + 2, yPosition + 2, 0f, 0f, 10, 10, 10f, 10f)
            }
            GlStateManager.color(color.r / 255f, color.g / 255f, color.b / 255f, color.alpha)
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
            GlStateManager.popMatrix()
        }
    }

    inner class SearchTextField : GuiTextField(
        69420,
        mc.fontRendererObj,
        OmniResolution.scaledWidth * 4 / 5 - 60,
        OmniResolution.scaledHeight - 26,
        OmniResolution.scaledWidth / 5,
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
