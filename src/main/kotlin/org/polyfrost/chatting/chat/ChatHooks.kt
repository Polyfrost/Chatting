package org.polyfrost.chatting.chat

import net.minecraft.client.gui.GuiTextField

object ChatHooks {
    var draft = ""

    var commandDraft = ""

    var input = ""

    var inputRight = 0

    var textField: GuiTextField? = null

    fun resetDraft() {
        draft = ""
        commandDraft = ""
    }

    fun checkField(field: Any): Boolean {
        return field == textField
    }

    fun switchTab() {
        val current = ChatTabs.currentTabs.firstOrNull()
        if (current == null) {
            ChatTabs.currentTabs.add(ChatTabs.tabs[0])
        } else {
            val nextIndex = (ChatTabs.tabs.indexOf(current) + 1) % ChatTabs.tabs.size
            ChatTabs.currentTabs.clear()
            ChatTabs.currentTabs.add(ChatTabs.tabs[nextIndex])
        }
    }
}