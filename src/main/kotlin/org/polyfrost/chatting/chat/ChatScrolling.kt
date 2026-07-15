package org.polyfrost.chatting.chat

import org.polyfrost.chatting.config.ChattingConfig
import kotlin.math.abs
import kotlin.math.roundToInt

object ChatScrolling {

    var shouldSmooth = false

    private var current = 0f
    private var from = 0f
    private var to = 0f
    private var startNanos = 0L
    private var durationMs = 0f
    private var initialized = false

    private var frozen = 0

    fun step(actual: Int) {
        if (!ChattingConfig.smoothScrolling) {
            current = actual.toFloat()
            to = current
            frozen = actual
            shouldSmooth = false
            return
        }
        if (!initialized) {
            current = actual.toFloat()
            to = current
            initialized = true
        }
        if (actual.toFloat() != to) {
            if (shouldSmooth && abs(actual - current) > 1f) {
                from = current
                to = actual.toFloat()
                durationMs = ChattingConfig.smoothScrollingMs
                startNanos = System.nanoTime()
            } else {
                from = actual.toFloat()
                to = actual.toFloat()
                current = actual.toFloat()
            }
        }
        shouldSmooth = false
        current = if (durationMs <= 0f) {
            to
        } else {
            val x = ((System.nanoTime() - startNanos) / 1_000_000f / durationMs).coerceIn(0f, 1f)
            if (x >= 1f) to else from + (to - from) * (1f - (1f - x) * (1f - x)) // ease-out-quad
        }
        frozen = current.roundToInt()
    }

    fun shift(delta: Int) {
        if (!ChattingConfig.smoothScrolling || !initialized) return
        from += delta
        to += delta
        current += delta
        frozen = current.roundToInt()
    }

    fun pos(): Int = frozen
}
