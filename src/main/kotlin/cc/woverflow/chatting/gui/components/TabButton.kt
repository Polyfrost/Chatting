package cc.woverflow.chatting.gui.components

import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.config.ChattingConfig

class TabButton(buttonId: Int, x: Int, widthIn: Int, heightIn: Int, private val chatTab: ChatTab) :
    CleanButton(buttonId, { x }, {
        UResolution.scaledHeight - 26
    }, widthIn, heightIn, chatTab.name, { RenderType.values()[ChattingConfig.textRenderType] }, { packedFGColour: Int, enabled: Boolean, hovered: Boolean ->
        var j = chatTab.color ?: color
        if (packedFGColour != 0) {
            j = packedFGColour
        } else if (!enabled) {
            j = chatTab.selectedColor ?: selectedColor
        } else if (hovered) {
            j = chatTab.hoveredColor ?: hoveredColor
        }
        j
    }) {

    override fun onMousePress() {
        if (UKeyboard.isShiftKeyDown()) {
            if (ChatTabs.currentTabs.contains(chatTab)) {
                ChatTabs.currentTabs.remove(chatTab)
            } else {
                ChatTabs.currentTabs.add(chatTab)
            }
        } else {
            ChatTabs.currentTabs.clear()
            ChatTabs.currentTabs.add(chatTab)
        }
    }

    override fun isEnabled(): Boolean {
        return ChatTabs.currentTabs.contains(chatTab)
    }

    companion object {
        const val color: Int = 14737632
        const val hoveredColor: Int = 16777120
        const val selectedColor: Int = 10526880
    }
}