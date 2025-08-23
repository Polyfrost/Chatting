package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.libs.caffeine.cache.Cache
import cc.polyfrost.oneconfig.libs.caffeine.cache.Caffeine
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent
import net.minecraft.client.gui.ChatLine
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.chat.ChatTabs.currentTabs
import org.polyfrost.chatting.hook.ChatHook
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
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

    private val baseChatLines: List<ChatLine>
        get() = (UMinecraft.getChatGUI() as? GuiNewChatAccessor)?.drawnChatLines?.let {ArrayList(it)} ?: emptyList()

    var lastSearch: String = ""

    var filteredMessages: List<ChatLine> = emptyList()
        get() = field.ifEmpty { baseChatLines }

    fun updateFilteredMessages(search: String) {
        lastSearch = search
        filteredMessages = filterMessages(search)
    }

    @JvmStatic
    fun filterMessages(search: String): List<ChatLine> {
        val list: List<ChatLine> = baseChatLines
        val chatTabMessages = filterChatTabMessages(search)
        if (chatTabMessages != null) {
            return chatTabMessages
        }
        return filterMessages2(search, list)
    }

    @JvmStatic
    fun filterMessages2(search: String, list: List<ChatLine>): List<ChatLine> {
        if (search.isBlank()) return list
        val cached = cache.getIfPresent(search)
        return cached ?: run {
            cache.put(search, list.filter {
                UTextComponent.stripFormatting(it.chatComponent.unformattedText).lowercase()
                    .contains(search.lowercase())
            })
            cache.getIfPresent(search)
        }
    }

    @JvmStatic
    fun filterChatTabMessages(search: String): List<ChatLine>? {
        val currentTabs = currentTabs.firstOrNull()
        if (currentTabs?.messages?.isEmpty() == false) {
            val list: MutableList<ChatLine> = ArrayList()
            for (message in currentTabs.messages?: emptyList()) {
                ChatHook.lineVisible = true
                list.add(ChatLine(0, ChatComponentText(message), 0))
                ChatHook.lineVisible = false
            }
            return filterMessages2(search, list)
        }
        return null
    }
}