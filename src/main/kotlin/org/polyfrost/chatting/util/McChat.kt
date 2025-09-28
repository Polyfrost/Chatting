package org.polyfrost.chatting.util

import org.polyfrost.chatting.event.NewMessageEvent
import org.polyfrost.oneconfig.api.event.v1.EventManager

object McChat {

    var messages = ArrayList<MessageInfo>()

    fun addMessage(messageInfo: MessageInfo) {
        messages.add(messageInfo)
        EventManager.INSTANCE.post(NewMessageEvent(messageInfo))
    }
}