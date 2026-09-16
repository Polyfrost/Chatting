package org.polyfrost.chatting.chat

import net.minecraft.client.gui.ChatLine
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import org.polyfrost.chatting.hook.ChatHook

/** Lightweight, client-thread-safe replacement for the removed Deftu/Caffeine helpers. */
object ChatSearchingManager {
    private const val CACHE_SIZE = 256
    private val cache = object : LinkedHashMap<String, List<ChatLine>>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<ChatLine>>) = size > CACHE_SIZE
    }

    var lastSearch = ""

    @JvmStatic
    fun filterMessages(text: String, list: List<ChatLine>): List<ChatLine>? =
        filterChatTabMessages(lastSearch) ?: filterMessages2(text, list)

    @JvmStatic
    fun filterMessages2(text: String, list: List<ChatLine>): List<ChatLine> {
        if (text.isBlank()) return list
        val key = "$text\u0000${list.size}\u0000${list.firstOrNull()?.updatedCounter ?: -1}"
        return synchronized(cache) {
            cache.getOrPut(key) {
                list.filter {
                    EnumChatFormatting.getTextWithoutFormattingCodes(it.chatComponent.unformattedText)
                        .lowercase().contains(text.lowercase())
                }
            }
        }
    }

    @JvmStatic
    fun filterChatTabMessages(text: String): List<ChatLine>? {
        val currentTab = ChatTabs.currentTabs.firstOrNull()
        val messages = currentTab?.messages ?: return null
        if (messages.isEmpty()) return null
        return messages.map {
            ChatHook.lineVisible = true
            try {
                ChatLine(0, ChatComponentText(it), 0)
            } finally {
                ChatHook.lineVisible = false
            }
        }.let { filterMessages2(text, it) }
    }
}
