package org.polyfrost.chatting.chat

object ChatHooks {
    var draft = ""

    fun resetDraft() {
        draft = ""
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