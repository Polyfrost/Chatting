package org.polyfrost.chatting.core

import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.input.InputEvent
import dev.deftu.omnicore.api.client.events.input.InputState
import dev.deftu.omnicore.api.client.screen.isInChatScreen
import dev.deftu.omnicore.api.client.screen.isInScreen
import dev.deftu.omnicore.api.eventBus
import org.polyfrost.chatting.component.ChatButtonGroup
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.ScreenOpenEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.events.HudEditorToggleEvent
import org.polyfrost.polyui.utils.fastEach

object McChat {

    fun initialize() {
        eventBus.register(this)
        eventHandler { event: HudEditorToggleEvent ->
            chatComponents.fastEach {
                it.swap(event.open)
            }
        }
        eventHandler { event: MouseInputEvent.Moved ->
            val lastHovered = hoveredComponent
            hoveredComponent = null
            chatComponents.fastEach {
                if (it.isInside(event.x, event.y)) {
                    hoveredComponent = it
                    return@fastEach
                }
            }
            hoveredComponent?.let {
                val x = (event.x - it.x - it.width * it.scaleX) / mcScale
                ChatButtonGroup.update(x)
                it.getCurrentElement()
            }
            if (hoveredComponent != lastHovered) {
                lastHovered?.hoverExit()
                hoveredComponent?.hoverEnter()
            }
        }
        eventHandler { event: ScreenOpenEvent ->
            if (event.getScreen<Object?>() == null) {
                chatComponents.fastEach {
                    it.onClose()
                }
            }
        }
    }

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseButton) {
        if (event.state == InputState.RELEASED) return
        if (!isInScreen || !isInChatScreen) return
        chatComponents.fastEach {
            if (it == hoveredComponent) {
                it.click(event.button.code)
            } else {
                it.selectedElements.clear()
            }
        }
    }

    fun addMessage(chatLine: McChatLine) {
        val chatLineHook = chatLine as ChatLineHook
        if (currentSender != null) {
            getSkinFromProfile(currentSender)?.let {
                chatLineHook.`chatting$setChatHead`(it)
            }
        }
        if (HudManager.isEditing) return
        chatComponents.fastEach {
            it.addMessage(chatLine)
        }
    }

    //#if MC <= 1.16.5
    //$$ fun removeMessageById(id: Int) {
    //$$     if (HudManager.isEditing) return
    //$$     org.polyfrost.chatting.core.getMessages().forEach {
    //$$         if (it.id == id) {
    //$$             chatComponents.fastEach { chatComponent ->
    //$$                 chatComponent.removeMessage(it)
    //$$             }
    //$$         }
    //$$     }
    //$$ }
    //#endif

    fun refreshChat() {
        if (HudManager.isEditing) return
        chatComponents.fastEach {
            it.removeAllMessages(false)
            it.addAllMessages()
        }
    }

    fun clearMessages() {
        if (HudManager.isEditing) return
        chatComponents.fastEach {
            it.removeAllMessages(true)
        }
    }
}