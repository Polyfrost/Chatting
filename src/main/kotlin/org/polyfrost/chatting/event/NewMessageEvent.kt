package org.polyfrost.chatting.event

import org.polyfrost.chatting.util.MessageInfo
import org.polyfrost.oneconfig.api.event.v1.events.Event

class NewMessageEvent(val messageInfo: MessageInfo) : Event {
}