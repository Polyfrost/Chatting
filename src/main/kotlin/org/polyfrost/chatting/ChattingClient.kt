package org.polyfrost.chatting

import net.fabricmc.api.ClientModInitializer
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hud.ChatWindowHud
import org.polyfrost.oneconfig.api.hud.v1.HudManager

object ChattingClient : ClientModInitializer {

    override fun onInitializeClient() {
        ChattingConfig.preload() //pre-init

        ChatShortcuts.initialize()
        ChatTabs.initialize()

        HudManager.register(ChatWindowHud())
    }
}
