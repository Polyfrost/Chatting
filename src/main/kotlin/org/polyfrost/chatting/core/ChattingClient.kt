package org.polyfrost.chatting.core

import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.input.InputEvent
import dev.deftu.omnicore.api.client.events.input.InputState
import dev.deftu.omnicore.api.client.screen.isInChatScreen
import dev.deftu.omnicore.api.client.screen.isInScreen
import dev.deftu.omnicore.api.eventBus
import org.polyfrost.chatting.component.ChatWindow
import org.polyfrost.chatting.event.MouseActionEvent
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.UIManager

object ChattingClient {

    fun initialize() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
        HudManager.register(ChatWindow(preview = true))
        eventBus.register(this)

        eventHandler { event: MouseInputEvent.Moved ->
            if (!isInScreen || !isInChatScreen) return@eventHandler
            UIManager.INSTANCE.defaultInstance.inputManager.mouseMoved(event.x, event.y)
        }
    }

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseButton) {
        if (event.state == InputState.RELEASED) return
        println(event.button)
        if (!isInScreen || !isInChatScreen) return
        UIManager.INSTANCE.defaultInstance.inputManager.mouseOver.let {
            EventManager.INSTANCE.post(MouseActionEvent.Companion.Click(it, event.button.code))
        }
    }

}