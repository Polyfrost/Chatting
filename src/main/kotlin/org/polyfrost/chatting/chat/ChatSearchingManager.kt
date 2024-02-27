package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.libs.caffeine.cache.Cache
import cc.polyfrost.oneconfig.libs.caffeine.cache.Caffeine
import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent
import net.minecraft.client.gui.ChatLine
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.chat.ChatTabs.currentTabs
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
        if (text.isBlank()) return list
        val cached = cache.getIfPresent(text)
        return cached ?: run {
            cache.put(text, list.filter {
                UTextComponent.stripFormatting(it.chatComponent.unformattedText).lowercase()
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
                list.add(ChatLine(0, ChatComponentText(message), 0))
            }
            return filterMessages(text, list)
        }
        return null
    }
}