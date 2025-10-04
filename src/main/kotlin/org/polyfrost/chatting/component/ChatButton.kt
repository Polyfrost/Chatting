package org.polyfrost.chatting.component

import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.renderer.Renderer
import org.polyfrost.polyui.utils.image

abstract class ChatButton(val image: PolyImage) {

    var color = rgba(0, 0, 0, 0.25f)

    abstract fun onClick(chatComponent: ChatComponent)

    fun hoverEnter() {
        color = rgba(255, 255, 255, 0.25f)
    }

    fun hoverExit() {
        color = rgba(0, 0, 0, 0.25f)
    }

    fun render(renderer: Renderer, x: Float, size: Float) {
        renderer.rect(x, 0f, size, size, color)
    }

    class CopyButton() : ChatButton("assets/chatting/icon/copy.svg".image()) {
        override fun onClick(chatComponent: ChatComponent) {
            chatComponent.copyMessages()
        }
    }
}