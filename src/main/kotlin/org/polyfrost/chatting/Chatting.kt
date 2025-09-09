package org.polyfrost.chatting

import net.fabricmc.api.ClientModInitializer
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.hud.v1.HudManager

object Chatting : ClientModInitializer {

    override fun onInitializeClient() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
        HudManager.register(ChatWindow(preview = true))
    }

}
