package org.polyfrost.chatting.chat

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hook.ChatComponentHook
import org.polyfrost.compose.render.PolyColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Builds and applies the timestamp prefix shown on the first line of each chat message.
 *
 * The prefix is applied at line-wrapping time rather than being baked into stored message content, so
 * toggling the feature or changing the format and calling [refresh] re-renders every visible message.
 */
object ChatTimestamps {

    private interface Prefix {
        fun render(timestampMillis: Long): Component?
    }

    private var cached: Prefix? = null

    /**
     * Returns [original] unchanged when the feature is disabled or the built prefix is empty; otherwise a
     * new component with the colored prefix prepended.
     */
    fun prepend(timestampMillis: Long, original: Component): Component {
        if (!ChattingConfig.chatTimestamps) return original
        val prefix = prefix(timestampMillis) ?: return original
        return Component.empty().append(prefix).append(original)
    }

    private fun prefix(timestampMillis: Long): Component? {
        val prefix = cached ?: build().also { cached = it }
        return prefix.render(timestampMillis)
    }

    /** Drops the cached prefix so the next render rebuilds it from the current config. */
    fun invalidate() {
        cached = null
    }

    /** Re-wraps all visible messages so the timestamp change takes effect immediately. */
    fun refresh() {
        //? if >=26.2 {
        val chat = Minecraft.getInstance().gui?.hud?.chat ?: return
        //?} else {
        /*val chat = Minecraft.getInstance().gui?.chat ?: return
        *///?}
        (chat as? ChatComponentHook)?.`chatting$refresh`()
    }

    private fun build(): Prefix =
        if (ChattingConfig.timestampMode == 1) buildCustom() else buildPreset()

    private fun styleOf(color: PolyColor): Style =
        Style.EMPTY.withColor(TextColor.fromRgb(color.argb and 0xFFFFFF))

    private fun zoned(timestampMillis: Long) =
        Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault())

    // region Preset

    private fun buildPreset(): Prefix {
        val dateFormatter = datePattern()?.let { DateTimeFormatter.ofPattern(it) }
        val timeFormatter = DateTimeFormatter.ofPattern(timePattern())
        val (open, close) = when (ChattingConfig.timestampDelimiter) {
            1 -> "[" to "]"
            2 -> "(" to ")"
            else -> "" to ""
        }
        return PresetPrefix(
            dateFormatter, timeFormatter, open, close,
            styleOf(ChattingConfig.timestampDateColor),
            styleOf(ChattingConfig.timestampTimeColor),
            styleOf(ChattingConfig.timestampDelimiterColor),
        )
    }

    private fun datePattern(): String? = when (ChattingConfig.timestampDate) {
        1 -> "yyyy-MM-dd"
        2 -> "dd/MM/yyyy"
        3 -> "dd/MM/yy"
        4 -> "dd/MM"
        5 -> "MM/dd/yyyy"
        6 -> "MM/dd/yy"
        7 -> "MM/dd"
        else -> null
    }

    private fun timePattern(): String {
        val hour = if (ChattingConfig.timestamp24Hour) {
            if (ChattingConfig.timestampLeadingZero) "HH" else "H"
        } else {
            if (ChattingConfig.timestampLeadingZero) "hh" else "h"
        }
        val body = if (ChattingConfig.timestampTime == 1) "$hour:mm:ss" else "$hour:mm"
        return if (ChattingConfig.timestamp24Hour) body else "$body a"
    }

    private class PresetPrefix(
        private val dateFormatter: DateTimeFormatter?,
        private val timeFormatter: DateTimeFormatter,
        private val open: String,
        private val close: String,
        private val dateStyle: Style,
        private val timeStyle: Style,
        private val delimStyle: Style,
    ) : Prefix {
        override fun render(timestampMillis: Long): Component {
            val time = zoned(timestampMillis)
            val prefix = Component.empty()
            if (open.isNotEmpty()) prefix.append(Component.literal(open).setStyle(delimStyle))
            if (dateFormatter != null) {
                prefix.append(Component.literal(dateFormatter.format(time)).setStyle(dateStyle))
                prefix.append(Component.literal(" "))
            }
            prefix.append(Component.literal(timeFormatter.format(time)).setStyle(timeStyle))
            if (close.isNotEmpty()) prefix.append(Component.literal(close).setStyle(delimStyle))
            prefix.append(Component.literal(" "))
            return prefix
        }
    }

    // endregion

    // region Custom

    private class CustomToken(val style: Style, val formatter: DateTimeFormatter?, val raw: String)

    private fun buildCustom(): Prefix {
        val format = ChattingConfig.timestampCustomFormat
        val tokens = ArrayList<CustomToken>()
        var style = Style.EMPTY
        var i = 0
        val chunk = StringBuilder()

        fun flushChunk() {
            if (chunk.isEmpty()) return
            val raw = chunk.toString()
            chunk.setLength(0)
            val formatter = runCatching { DateTimeFormatter.ofPattern(raw) }.getOrNull()
            tokens.add(CustomToken(style, formatter, raw))
        }

        while (i < format.length) {
            val c = format[i]
            if (c == '&' && i + 1 < format.length && isColorCode(format[i + 1])) {
                flushChunk()
                style = applyCode(style, format[i + 1])
                i += 2
                continue
            }
            if (c == '<' && i + 8 < format.length && format[i + 1] == '#' && format[i + 8] == '>' &&
                isHex(format, i + 2, 6)
            ) {
                flushChunk()
                val rgb = format.substring(i + 2, i + 8).toInt(16)
                style = style.withColor(TextColor.fromRgb(rgb))
                i += 9
                continue
            }
            chunk.append(c)
            i++
        }
        flushChunk()
        return CustomPrefix(tokens)
    }

    private fun isColorCode(c: Char): Boolean =
        c in '0'..'9' || c in 'a'..'f' || c in 'k'..'o' || c == 'r' ||
            c in 'A'..'F' || c in 'K'..'O' || c == 'R'

    private fun isHex(s: String, start: Int, length: Int): Boolean {
        for (j in start until start + length) {
            val c = s[j]
            if (c !in '0'..'9' && c !in 'a'..'f' && c !in 'A'..'F') return false
        }
        return true
    }

    private fun applyCode(style: Style, code: Char): Style {
        val lower = code.lowercaseChar()
        if (lower == 'r') return Style.EMPTY
        val format = ChatFormatting.getByCode(lower) ?: return style
        return style.applyFormat(format)
    }

    private class CustomPrefix(private val tokens: List<CustomToken>) : Prefix {
        override fun render(timestampMillis: Long): Component? {
            if (tokens.isEmpty()) return null
            val time = zoned(timestampMillis)
            val prefix = Component.empty()
            for (token in tokens) {
                val text = if (token.formatter != null) {
                    runCatching { token.formatter.format(time) }.getOrDefault(token.raw)
                } else {
                    token.raw
                }
                prefix.append(Component.literal(text).setStyle(token.style))
            }
            return prefix
        }
    }

    // endregion
}
