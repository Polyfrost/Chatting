package com.raeids.stratus.hook

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

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

val cache: Cache<String, List<ChatLine>> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()

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

fun setPrevText(text: String) {
    (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).prevText = text
}