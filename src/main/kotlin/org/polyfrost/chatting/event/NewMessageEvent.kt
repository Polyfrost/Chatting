package org.polyfrost.chatting.event

import net.minecraft.client.gui.hud.ChatHudLine
import org.polyfrost.oneconfig.api.event.v1.events.Event

class NewMessageEvent(val message: ChatHudLine) : Event {
}