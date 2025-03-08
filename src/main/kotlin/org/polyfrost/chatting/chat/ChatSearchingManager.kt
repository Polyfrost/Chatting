package org.polyfrost.chatting.chat

import dev.deftu.textile.minecraft.MinecraftTextFormat
import org.polyfrost.oneconfig.libs.caffeine.cache.Cache
import org.polyfrost.oneconfig.libs.caffeine.cache.Caffeine
import net.minecraft.client.gui.ChatLine
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.chat.ChatTabs.currentTabs
import org.polyfrost.chatting.hook.ChatHook
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object ChatSearchingManager {
    private var counter: AtomicInteger = AtomicInteger(0)
    private var POOL: ThreadPoolExecutor = ThreadPoolExecutor(
        50, 50,
        0L, TimeUnit.SECONDS,
        LinkedBlockingQueue()
    ) { r ->
        Thread(
            r,
            "Chat Filter Cache Thread ${counter.incrementAndGet()}"
        )
    }

    @JvmStatic
    val cache: Cache<String, List<ChatLine>> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()

    var lastSearch = ""

    @JvmStatic
    fun filterMessages(text: String, list: List<ChatLine>): List<ChatLine>? {
        val chatTabMessages = filterChatTabMessages(lastSearch)
        if (chatTabMessages != null) {
            return chatTabMessages
        }
        return filterMessages2(text, list)
    }

    @JvmStatic
    fun filterMessages2(text: String, list: List<ChatLine>): List<ChatLine>? {
        if (text.isBlank()) return list
        val cached = cache.getIfPresent(text)
        return cached ?: run {
            cache.put(text, list.filter {
                MinecraftTextFormat.stripFormat(it.chatComponent.unformattedText).lowercase()
                    .contains(text.lowercase())
            })
            cache.getIfPresent(text)
        }
    }

    @JvmStatic
    fun filterChatTabMessages(text: String): List<ChatLine>? {
        val currentTabs = currentTabs.firstOrNull()
        if (currentTabs?.messages?.isEmpty() == false) {
            val list: MutableList<ChatLine> = ArrayList()
            for (message in currentTabs.messages?: emptyList()) {
                ChatHook.lineVisible = true
                list.add(ChatLine(0, ChatComponentText(message), 0))
                ChatHook.lineVisible = false
            }
            return filterMessages2(text, list)
        }
        return null
    }
}