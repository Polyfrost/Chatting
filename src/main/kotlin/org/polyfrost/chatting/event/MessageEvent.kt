package org.polyfrost.chatting.event

import org.polyfrost.chatting.util.MessageInfo
import org.polyfrost.oneconfig.api.event.v1.events.Event

class MessageEvent {

    class Add(val messageInfo: MessageInfo) : Event

    class Remove(val messageInfo: MessageInfo) : Event

    class Clear() : Event

}