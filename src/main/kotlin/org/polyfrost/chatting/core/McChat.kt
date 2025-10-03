package org.polyfrost.chatting.core

import org.polyfrost.chatting.event.MessageEvent
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.oneconfig.api.event.v1.EventManager

object McChat {

    fun addMessage(chatLine: McChatLine) {
        val chatLineHook = chatLine as ChatLineHook
        if (currentSender != null) {
            getSkinFromProfile(currentSender)?.let {
                chatLineHook.`chatting$setChatHead`(it)
            }
        }
        EventManager.INSTANCE.post(MessageEvent.Add(chatLine))
    }

    //#if MC <= 1.16.5
    //$$ fun removeMessageById(id: Int) {
    //$$     org.polyfrost.chatting.core.getMessages().forEach {
    //$$         if (it.id == id) {
    //$$             EventManager.INSTANCE.post(MessageEvent.Remove(it))
    //$$         }
    //$$     }
    //$$ }
    //#endif

    fun refreshChat() {
        EventManager.INSTANCE.post(MessageEvent.Refresh())
    }

    fun clearMessages() {
        EventManager.INSTANCE.post(MessageEvent.Clear())
    }
}