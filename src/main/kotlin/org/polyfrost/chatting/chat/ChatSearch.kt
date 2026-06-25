package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hook.ChatComponentHook

object ChatSearch {

    var enabled = false
        private set

    var query = ""
        private set

    fun shouldFilter(): Boolean = ChattingConfig.chatSearch && enabled && query.isNotBlank()
            //? if >=26.2 {
            /*&& Minecraft.getInstance().gui.screen() is ChatScreen
            *///?} else {
            && Minecraft.getInstance().screen is ChatScreen
            //?}

    fun matches(message: Component): Boolean =
        message.string.lowercase().contains(query.lowercase())

    fun toggle() {
        enabled = !enabled
        query = ""
        refresh()
    }

    fun close() {
        if (!enabled && query.isEmpty()) return
        enabled = false
        query = ""
        refresh()
    }

    fun setQuery(text: String) {
        if (text == query) return
        query = text
        refresh()
    }

    fun refresh() {
        //? if >=26.2 {
        /*val chat = Minecraft.getInstance().gui?.hud?.chat ?: return
        *///?} else {
        val chat = Minecraft.getInstance().gui?.chat ?: return
        //?}
        (chat as? ChatComponentHook)?.`chatting$refresh`()
    }
}
