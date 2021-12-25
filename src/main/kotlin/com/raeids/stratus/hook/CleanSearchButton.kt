package com.raeids.stratus.hook

import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiTextField

class CleanSearchButton: CleanButton(3993935, {UResolution.scaledWidth - 42}, {UResolution.scaledHeight - 27}, 40, 12, "Search") {
    val inputField = SearchTextField()
    private var chatBox = false

    override fun isEnabled(): Boolean {
        return chatBox
    }

    override fun onMousePress() {
        println("hi")
        chatBox = !chatBox
        inputField.setEnabled(chatBox)
        inputField.isFocused = chatBox
        (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).prevText = ""
        inputField.text = ""
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        inputField.drawTextBox()
        super.drawButton(mc, mouseX, mouseY)
    }

    inner class SearchTextField: GuiTextField(69420, Minecraft.getMinecraft().fontRendererObj, UResolution.scaledWidth * 4 / 5 - 60, UResolution.scaledHeight - 27, UResolution.scaledWidth / 5, 12) {

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