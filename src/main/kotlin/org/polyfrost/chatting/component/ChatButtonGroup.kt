package org.polyfrost.chatting.component

import org.polyfrost.chatting.core.mcScale
import org.polyfrost.polyui.renderer.Renderer

object ChatButtonGroup {

    val buttons = arrayListOf(ChatButton.CopyButton())

    fun update(x: Float) {
        buttons.forEachIndexed { i, button ->
            if (x in 1f + 10 * i..9f + 10 * i) {
                button.hoverEnter()
            } else {
                button.hoverExit()
            }
        }
    }

    fun render(renderer: Renderer, x: Float, size: Float) {
        buttons.forEachIndexed { i, button ->
            button.render(renderer, x + i * 10 * mcScale, size)
        }
    }
}