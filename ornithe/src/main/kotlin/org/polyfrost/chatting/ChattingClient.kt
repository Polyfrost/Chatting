package org.polyfrost.chatting

import net.minecraft.client.Minecraft
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig

/**
 * Client lifecycle owner. Feature modules initialize here rather than through
 * the global Chatting state object, matching modern Chatting's architecture.
 */
object ChattingClient {
    private var initialized = false
    private var deferredFeaturesInitialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        ChattingConfig.preload()
        MinecraftClientEvents.TICK_END.register(::initializeDeferredFeatures)
    }

    private fun initializeDeferredFeatures(client: Minecraft) {
        if (deferredFeaturesInitialized) return
        ChatTabs.initialize()
        ChatShortcuts.initialize()
        deferredFeaturesInitialized = true
    }
}
