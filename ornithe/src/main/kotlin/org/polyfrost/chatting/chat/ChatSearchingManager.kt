package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting

/** Lightweight, client-thread-safe replacement for the removed Deftu/Caffeine helpers. */
object ChatSearchingManager {
    private const val CACHE_SIZE = 256
    private val cache = object : LinkedHashMap<String, List<ChatLine>>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<ChatLine>>) = size > CACHE_SIZE
    }

    var lastSearch = ""

    @JvmStatic
    fun clearCache() = synchronized(cache) { cache.clear() }

    /**
     * Changes the active query as one renderer operation: filtering is cached,
     * so a new query must discard stale entries and return the chat to its
     * newest line rather than leaving its old scroll offset out of range.
     */
    @JvmStatic
    fun setQuery(query: String) {
        if (lastSearch == query) return
        lastSearch = query
        clearCache()
        Minecraft.getMinecraft().ingameGUI.chatGUI.resetScroll()
    }

    @JvmStatic
    fun filterMessages(text: String, list: List<ChatLine>): List<ChatLine>? =
        filterChatTabMessages(text) ?: filterMessages2(text, list)

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
        return messages.map { ChatLine(0, ChatComponentText(it), 0) }
            .let { filterMessages2(text, it) }
    }
}
