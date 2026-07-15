package org.polyfrost.chatting

import net.fabricmc.api.ClientModInitializer
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
        ChattingConfig.preload() //pre-init

        ChatShortcuts.initialize()
        ChatTabs.initialize()

        HudManager.register(ChatWindowHud())

        EventManager.register(ServerJoinEvent::class.java, Runnable { TextTunnelsCompat.reevaluate() })
        EventManager.register(ServerJoinEvent::class.java, Runnable { ChatHeadsCompat.reevaluate() })
        EventManager.register(ServerJoinEvent::class.java, Runnable { ChatImpressiveAnimationCompat.reevaluate() })
    }
}
