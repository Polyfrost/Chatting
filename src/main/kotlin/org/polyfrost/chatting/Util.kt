@file:JvmName("Util")

package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.unit.seconds

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

fun OrderedText.asString(): String {
    val sb = StringBuilder()
    var lastStyle = Style.EMPTY

    this.accept { _, style, codePoint ->
        if (style != lastStyle) {
            sb.append(getFormattingCodes(style, lastStyle))
            lastStyle = style
        }
        sb.appendCodePoint(codePoint)
        true
    }

    return sb.toString()
}

private fun getFormattingCodes(style: Style, old: Style): String {
    val stringBuilder = StringBuilder()

    if (!style.isBold && old.isBold ||
        !style.isItalic && old.isItalic ||
        !style.isUnderlined && old.isUnderlined ||
        !style.isStrikethrough && old.isStrikethrough ||
        !style.isObfuscated && old.isObfuscated ||
        (style.color != old.color && style.color == null)) {
        stringBuilder.append("§r")
    }

    val color = style.color
    if (color != null) {
        val format = Formatting.byName(color.name)
        if (format != null) {
            stringBuilder.append('§').append(format.code)
        } else {
            val hex = String.format("%06x", color.rgb)
            stringBuilder.append("§x")
            for (c in hex) stringBuilder.append('§').append(c)
        }
    }

    if (style.isBold) stringBuilder.append("§l")
    if (style.isItalic) stringBuilder.append("§o")
    if (style.isUnderlined) stringBuilder.append("§n")
    if (style.isStrikethrough) stringBuilder.append("§m")
    if (style.isObfuscated) stringBuilder.append("§k")

    return stringBuilder.toString()
}

fun String.copyToClipboard() {
    mc.keyboard.clipboard = this
    Notifications.enqueue(Notifications.Type.Info, "Chatting", "Copied \"$this\" to clipboard.", 3.seconds)
}