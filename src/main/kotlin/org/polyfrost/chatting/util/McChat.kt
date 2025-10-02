package org.polyfrost.chatting.util

import dev.deftu.textile.minecraft.MCTextHolder
import net.minecraft.client.gui.hud.ChatHudLine
import org.polyfrost.chatting.event.MessageEvent
import org.polyfrost.oneconfig.api.event.v1.EventManager

object McChat {

    val messages = HashMap<Int, MessageInfo>()

    fun addMessage(
        chatLine:
        //#if MC == 11605
        //$$ ChatHudLine<net.minecraft.text.Text>,
        //#else
        ChatHudLine,
        //#endif
    ) {
        val info = MessageInfo(
            chatLine.hashCode(),
            MCTextHolder.convertFromVanilla(chatLine.comp_893),
            chatLine.creationTick,
            //#if MC > 1.16.5
            chatLine.indicator
            //#endif
        )
        messages[chatLine.hashCode()] = info
        EventManager.INSTANCE.post(MessageEvent.Add(info))
        while (messages.size > 100) {
            val id = messages.keys.first()
            messages.remove(id)
        }
    }

    fun removeMessage(
        chatLine:
        //#if MC == 11605
        //$$ ChatHudLine<net.minecraft.text.Text>,
        //#else
        ChatHudLine,
        //#endif
    ) {
        val info = messages[chatLine.hashCode()] ?: return
        EventManager.INSTANCE.post(MessageEvent.Remove(info))
        messages.remove(chatLine.hashCode())
    }

    fun clearMessages() {
        EventManager.INSTANCE.post(MessageEvent.Clear())
    }
}