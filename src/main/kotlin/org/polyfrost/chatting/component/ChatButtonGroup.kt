package org.polyfrost.chatting.component

import org.polyfrost.chatting.core.mcScale
import org.polyfrost.polyui.renderer.Renderer

class ChatButtonGroup(val buttons: ArrayList<ChatButton>) {

    var hoveredButton: ChatButton? = null

    fun update(x: Float) {
        hoveredButton = null
        buttons.forEachIndexed { i, button ->
            if (x in 1f + 10 * i..9f + 10 * i) {
                button.hoverEnter()
                hoveredButton = button
            } else {
                button.hoverExit()
            }
        }
    }

    fun render(renderer: Renderer, x: Float = 0f, y: Float = 0f) {
        val size = 9 * mcScale
        buttons.forEachIndexed { i, button ->
            button.render(renderer, x + i * 10 * mcScale, y, size)
        }
    }
}