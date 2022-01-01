package cc.woverflow.chattils.gui.components

import cc.woverflow.chattils.Chattils
import cc.woverflow.chattils.hook.GuiNewChatHook
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class SearchButton :
    CleanButton(3993935, { UResolution.scaledWidth - 14 }, { UResolution.scaledHeight - 27 }, 12, 12, "") {
    val inputField = SearchTextField()
    private var chatBox = false

    override fun isEnabled(): Boolean {
        return chatBox
    }

    override fun onMousePress() {
        chatBox = !chatBox
        inputField.setEnabled(chatBox)
        inputField.isFocused = chatBox
        (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).prevText = ""
        inputField.text = ""
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        inputField.drawTextBox()
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            mc.textureManager.bindTexture(ResourceLocation(Chattils.ID, "search.png"))
            if (isEnabled()) {
                GlStateManager.color(224f / 255f, 224f / 255f, 224f / 255f)
            } else if (mouseX >= xPosition && mouseX <= xPosition + 10 && mouseY >= yPosition && mouseY <= yPosition + 10) {
                GlStateManager.color(1f, 1f, 160f / 255f)
            } else {
                GlStateManager.color(1f, 1f, 1f)
            }
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }

    inner class SearchTextField : GuiTextField(
        69420,
        Minecraft.getMinecraft().fontRendererObj,
        UResolution.scaledWidth * 4 / 5 - 60,
        UResolution.scaledHeight - 27,
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