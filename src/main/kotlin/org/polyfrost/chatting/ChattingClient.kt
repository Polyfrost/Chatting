package org.polyfrost.chatting

//? if > 1.8.9 {
import net.fabricmc.api.ClientModInitializer
import org.polyfrost.chatting.chat.ChatScreenshot
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.compat.ChatHeadsCompat
import org.polyfrost.chatting.compat.ChatImpressiveAnimationCompat
import org.polyfrost.chatting.compat.TextTunnelsCompat
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hud.ChatWindowHud
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.events.ServerJoinEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager

object ChattingClient : ClientModInitializer {

    override fun onInitializeClient() {
        ChatScreenshot.allowAwtClipboard()

        ChattingConfig.preload()

        ChatShortcuts.initialize()
        ChatTabs.initialize()

        HudManager.register(ChatWindowHud())

        EventManager.register(ServerJoinEvent::class.java, Runnable { TextTunnelsCompat.reevaluate() })
        EventManager.register(ServerJoinEvent::class.java, Runnable { ChatHeadsCompat.reevaluate() })
        EventManager.register(ServerJoinEvent::class.java, Runnable { ChatImpressiveAnimationCompat.reevaluate() })
    }
}
//?} else {
/*import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatScreenshot
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig

/**
 * Client lifecycle owner. Feature modules initialize here rather than through
 * the global Chatting state object, matching modern Chatting's architecture.
 */
object ChattingClient : ClientModInitializer {
    private var deferredFeaturesInitialized = false

    override fun onInitializeClient() {
        ChatScreenshot.allowAwtClipboard()
        ChattingConfig.preload()
        ChatScreenshot.initialize()
        MinecraftClientEvents.TICK_END.register(::initializeDeferredFeatures)
    }

    private fun initializeDeferredFeatures(client: Minecraft) {
        if (deferredFeaturesInitialized) return
        ChatTabs.initialize()
        ChatShortcuts.initialize()
        deferredFeaturesInitialized = true
    }
}
*///?}
