package org.polyfrost.chatting.chat

import net.minecraft.util.FormattedCharSequence
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.utils.v1.dsl.mc

object SmoothChat {

    private const val LINE_HEIGHT = 9f

    private var startNanos = 0L
    private var durationMs = 0f

    var animating = false
        private set

    private val newLines = HashSet<FormattedCharSequence>()

    fun start() {
        newLines.clear()
        if (!ChattingConfig.smoothChat) {
            animating = false
            return
        }
        durationMs = (1f - ChattingConfig.messageSpeed) * 1000f
        animating = durationMs > 0f
        startNanos = System.nanoTime()
    }

    fun addLine(line: FormattedCharSequence) {
        if (animating) newLines.add(line)
    }

    private fun percent(): Float {
        if (!animating) return 1f
        if (durationMs <= 0f) {
            animating = false
            return 1f
        }
        val elapsed = (System.nanoTime() - startNanos) / 1_000_000f
        if (elapsed >= durationMs) {
            animating = false
            return 1f
        }
        val x = elapsed / durationMs
        val inv = x - 1f
        return -1f * inv * inv * inv * inv + 1f
    }

    fun translateY(scrolled: Boolean): Float {
        if (!ChattingConfig.smoothChat || scrolled || !animating) return 0f
        val p = percent()
        if (p >= 1f) return 0f
        val scale = mc.options.chatScale().get().toFloat()
        return LINE_HEIGHT * (1f - p) * scale
    }

    fun fade(line: FormattedCharSequence, alpha: Float): Float =
        if (animating && newLines.contains(line)) alpha * percent() else alpha

    fun fadeColor(line: FormattedCharSequence, color: Int): Int {
        if (!animating || !newLines.contains(line)) return color
        val a = ((color ushr 24) and 0xFF) * percent()
        return (color and 0x00FFFFFF) or (a.toInt().coerceIn(0, 255) shl 24)
    }
}
