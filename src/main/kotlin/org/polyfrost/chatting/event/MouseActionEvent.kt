package org.polyfrost.chatting.event

import org.polyfrost.oneconfig.api.event.v1.events.Event
import org.polyfrost.polyui.component.Inputtable

class MouseActionEvent : Event {

    companion object {
        class Click(val mouseOver: Inputtable?, val button: Int) : Event
    }

}