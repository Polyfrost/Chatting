package org.polyfrost.chatting.event

import org.polyfrost.chatting.McChatLine
import org.polyfrost.oneconfig.api.event.v1.events.Event

class MessageEvent {

    class Add(val chatLine: McChatLine) : Event

    class Remove(val chatLine: McChatLine) : Event

    class Clear() : Event

    class Refresh() : Event

}