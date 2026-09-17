package org.polyfrost.chatting

import net.minecraft.client.Minecraft
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig
import java.nio.file.Paths

/** Fabric (Ornithe) client bootstrap for the v2 feature set. */
object Chatting {
    const val ID = "chatting"
    const val NAME = "Chatting"
    const val VER = "2.0.6"

    val oldModDir = Paths.get("W-OVERFLOW", NAME)

    var peeking = false
        get() = ChattingConfig.chatPeek && field

    private var clientInitialized = false

    fun initClient() {
        // OneConfig can register its option tree before Minecraft exists, but tab
        // buttons require the client font renderer. Defer those to the first tick.
        ChattingConfig.preload()
        MinecraftClientEvents.TICK_END.register(::finishClientInitialization)
    }

    private fun finishClientInitialization(client: Minecraft) {
        if (clientInitialized) return
        ChatTabs.initialize()
        ChatShortcuts.initialize()
        clientInitialized = true
    }

    fun getChatHeight(opened: Boolean): Int =
        if (opened) ChattingConfig.focusedHeight else ChattingConfig.unfocusedHeight

    fun getChatWidth(): Int = ChattingConfig.customWidth
}
