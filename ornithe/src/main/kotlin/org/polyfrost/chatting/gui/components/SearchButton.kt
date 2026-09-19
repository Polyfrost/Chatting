package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import org.polyfrost.chatting.chat.ChatSearchingManager
import org.polyfrost.chatting.config.ChattingConfig

class SearchButton : CleanButton(
    3993935, { ScaledResolution(Minecraft.getMinecraft()).scaledWidth - 14 }, 12, 12, "", { RenderType.NONE },
) {
    val inputField = SearchTextField()
    private var chatBox = false

    override fun isEnabled() = chatBox

    override fun onMousePress() {
        chatBox = !chatBox
        inputField.setEnabled(chatBox)
        inputField.isFocused = chatBox
        ChatSearchingManager.setQuery("")
        inputField.text = ""
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        val resolution = ScaledResolution(mc)
        inputField.xPosition = resolution.scaledWidth * 4 / 5 - 60
        inputField.yPosition = resolution.scaledHeight - 26
        inputField.drawTextBox()
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            val color = if (isEnabled()) 0xFFC8C8C8.toInt() else if (hovered) ChattingConfig.chatButtonHoveredColor.argb else ChattingConfig.chatButtonColor.argb
            drawIcon(mc, SEARCH, color)
        }
    }

    inner class SearchTextField : GuiTextField(
        69420,
        Minecraft.getMinecraft().fontRendererObj,
        0,
        0,
        ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 5,
        12,
    ) {
        init {
            maxStringLength = 100
            enableBackgroundDrawing = true
            isFocused = false
            setCanLoseFocus(true)
        }

        override fun drawTextBox() {
            if (isEnabled()) {
                if (!isFocused) isFocused = true
                super.drawTextBox()
            }
        }
    }

    companion object {
        private val SEARCH = ResourceLocation("chatting", "search.png")
    }
}
