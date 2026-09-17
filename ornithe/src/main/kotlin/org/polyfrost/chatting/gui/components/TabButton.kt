package org.polyfrost.chatting.gui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import org.polyfrost.chatting.chat.ChatTab
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig

class TabButton(buttonId: Int, x: Int, widthIn: Int, heightIn: Int, private val chatTab: ChatTab) :
    CleanButton(buttonId, { x }, widthIn, heightIn, chatTab.name, { RenderType.values()[ChattingConfig.textRenderType] }, { packed, enabled, hovered ->
        when {
            packed != 0 -> packed
            !enabled -> chatTab.selectedColor ?: selectedColor
            hovered -> chatTab.hoveredColor ?: hoveredColor
            else -> chatTab.color ?: color
        }
    }) {
    override fun onMousePress() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            if (!ChatTabs.currentTabs.remove(chatTab)) ChatTabs.currentTabs.add(chatTab)
        } else {
            ChatTabs.currentTabs.clear()
            ChatTabs.currentTabs.add(chatTab)
        }
    }

    override fun setPositionY() {
        yPosition = ScaledResolution(Minecraft.getMinecraft()).scaledHeight - 26
    }

    override fun isEnabled() = ChatTabs.currentTabs.contains(chatTab)

    companion object {
        const val color = 14737632
        const val hoveredColor = 16777120
        const val selectedColor = 10526880
    }
}
