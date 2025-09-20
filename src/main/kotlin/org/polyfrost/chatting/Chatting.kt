package org.polyfrost.chatting

import dev.deftu.omnicore.client.OmniScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.gui.screen.ChatScreen
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.event.MouseActionEvent
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.event.v1.EventManager
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
        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            if (screen !is ChatScreen) return@register
            ScreenMouseEvents.afterMouseClick(screen).register { _, _, _, button ->
                UIManager.INSTANCE.defaultInstance.inputManager.mouseOver.let {
                    EventManager.INSTANCE.post(MouseActionEvent.Companion.Click(it, button))
                }
            }
            ScreenMouseEvents.afterMouseScroll(screen).register { _, _, mouseY, horizontalAmount, verticalAmount ->
                UIManager.INSTANCE.defaultInstance.inputManager.mouseOver?.let {
                    if (it is ChatComponent) {
                        it.scroll(verticalAmount.toFloat())
                    }
                }
            }
        }
    }

}
