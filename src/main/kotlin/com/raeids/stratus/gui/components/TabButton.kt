package com.raeids.stratus.gui.components

import com.raeids.stratus.chat.ChatTab
import com.raeids.stratus.chat.ChatTabs
import gg.essential.universal.UResolution

class TabButton(buttonId: Int, x: Int, widthIn: Int, heightIn: Int, private val chatTab: ChatTab) :
    CleanButton(buttonId, { x }, {
        UResolution.scaledHeight - 26
    }, widthIn, heightIn, chatTab.name) {

    override fun onMousePress() {
        ChatTabs.currentTab = chatTab
    }

    override fun isEnabled(): Boolean {
        return chatTab != ChatTabs.currentTab
    }
}