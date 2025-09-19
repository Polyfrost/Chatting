package org.polyfrost.chatting

import dev.deftu.omnicore.client.OmniScreen
import net.fabricmc.api.ClientModInitializer
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.UIManager

object Chatting : ClientModInitializer {

    override fun onInitializeClient() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
        HudManager.register(ChatWindow(preview = true))
        eventHandler { event: MouseInputEvent.Moved ->
            if (!OmniScreen.isInScreen || !OmniScreen.isInChat) return@eventHandler
            UIManager.INSTANCE.defaultInstance.inputManager.mouseMoved(event.x, event.y)
        }
        eventHandler { event: MouseInputEvent ->
            if (!OmniScreen.isInScreen || !OmniScreen.isInChat) return@eventHandler
            if (event.state == 0) {
                println("released ${event.button}")
                UIManager.INSTANCE.defaultInstance.inputManager.mouseReleased(event.button)
            } else {
                println("pressed ${event.button}")
                UIManager.INSTANCE.defaultInstance.inputManager.mousePressed(event.button)
            }
        }
    }

}
