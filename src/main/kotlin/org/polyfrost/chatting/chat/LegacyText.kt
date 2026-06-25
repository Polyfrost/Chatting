package org.polyfrost.chatting.chat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.util.FormattedCharSequence
import java.util.Optional

/**
 * [Component] but with "§" codes for chat tabs
 */
object LegacyText {

    //? if >=26.2 {
    /*// 26.2 stripped the color metadata off ChatFormatting; TextColor.fromLegacyFormat now
    // resolves the RGB for the 16 color codes (and returns null for the format-only codes).
    private val byColor: Map<Int, ChatFormatting> =
        ChatFormatting.values().mapNotNull { cf -> TextColor.fromLegacyFormat(cf)?.let { it.value to cf } }.toMap()
    *///?} else {
    private val byColor: Map<Int, ChatFormatting> =
        ChatFormatting.values().filter { it.isColor }.associateBy { it.color!! }
    //?}

    fun toFormatted(component: Component): String {
        val sb = StringBuilder()
        component.visit(FormattedText.StyledContentConsumer<Unit> { style, text ->
            sb.append(codes(style)).append(text).append(ChatFormatting.RESET)
            Optional.empty()
        }, Style.EMPTY)
        return sb.toString()
    }

    fun toFormatted(sequence: FormattedCharSequence): String {
        val sb = StringBuilder()
        var lastCodes: String? = null
        sequence.accept { _, style, codePoint ->
            val codes = ChatFormatting.RESET.toString() + codes(style)
            if (codes != lastCodes) {
                sb.append(codes)
                lastCodes = codes
            }
            sb.appendCodePoint(codePoint)
            true
        }
        return sb.toString()
    }

    private fun codes(style: Style): String {
        val sb = StringBuilder()
        style.color?.let { tc -> byColor[tc.value]?.let { sb.append(it) } }
        if (style.isBold) sb.append(ChatFormatting.BOLD)
        if (style.isStrikethrough) sb.append(ChatFormatting.STRIKETHROUGH)
        if (style.isUnderlined) sb.append(ChatFormatting.UNDERLINE)
        if (style.isItalic) sb.append(ChatFormatting.ITALIC)
        if (style.isObfuscated) sb.append(ChatFormatting.OBFUSCATED)
        return sb.toString()
    }
}
