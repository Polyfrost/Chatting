package org.polyfrost.chatting.component

import org.polyfrost.chatting.core.McChat
import org.polyfrost.chatting.core.mcScale
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.renderer.Renderer
import org.polyfrost.polyui.utils.image

abstract class ChatButton(val image: PolyImage?) {

    var color = rgba(0, 0, 0, 0.25f)

    abstract fun onClick(chatComponent: ChatComponent)

    fun hoverEnter() {
        color = rgba(255, 255, 255, 0.25f)
    }

    fun hoverExit() {
        color = rgba(0, 0, 0, 0.25f)
    }

    fun render(renderer: Renderer, x: Float, y: Float, size: Float) {
        renderer.rect(x, y, size, size, color)
        image?.let {
            renderer.image(it, x + mcScale, y + mcScale, 7 * mcScale, 7 * mcScale)
        }
    }

    class CopyButton() : ChatButton("assets/chatting/icon/copy.svg".image()) {
        override fun onClick(chatComponent: ChatComponent) {
            chatComponent.copyMessages()
        }
    }

    class ScreenShotButton(): ChatButton(null) {
        override fun onClick(chatComponent: ChatComponent) {
        }
    }

    class DeleteButton(): ChatButton(null) {
        override fun onClick(chatComponent: ChatComponent) {
            McChat.deleteMessages(chatComponent.getSelected().map { it.fullMessage })
            chatComponent.selectedElements.clear()
        }
    }
}