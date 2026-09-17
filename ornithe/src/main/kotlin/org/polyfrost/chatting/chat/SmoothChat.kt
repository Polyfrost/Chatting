package org.polyfrost.chatting.chat

import org.polyfrost.chatting.config.ChattingConfig

/**
 * Modern Chatting's duration-based message animation, expressed without a
 * renderer dependency so each Minecraft generation can supply its own hooks.
 */
object SmoothChat {
    private const val LINE_HEIGHT = 9f

    private var startNanos = 0L
    private var durationMs = 0f

    var animating = false
        private set

    fun start() {
        if (!ChattingConfig.smoothChat) {
            animating = false
            return
        }
        durationMs = ChattingConfig.smoothChatMs
        animating = durationMs > 0f
        startNanos = System.nanoTime()
    }

    fun translateY(scrolled: Boolean, chatScale: Float): Float {
        if (scrolled || !animating) return 0f
        val progress = percent()
        return if (progress >= 1f) 0f else LINE_HEIGHT * (1f - progress) * chatScale
    }

    fun fadeColor(color: Int): Int {
        if (!animating) return color
        val alpha = ((color ushr 24) and 0xFF) * percent()
        return (color and 0x00FFFFFF) or (alpha.toInt().coerceIn(0, 255) shl 24)
    }

    private fun percent(): Float {
        if (!animating || durationMs <= 0f) {
            animating = false
            return 1f
        }
        val elapsed = (System.nanoTime() - startNanos) / 1_000_000f
        if (elapsed >= durationMs) {
            animating = false
            return 1f
        }
        val inverse = elapsed / durationMs - 1f
        return -inverse * inverse * inverse * inverse + 1f
    }
}
