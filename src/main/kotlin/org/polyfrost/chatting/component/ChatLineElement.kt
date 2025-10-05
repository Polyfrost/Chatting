package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.textile.MutableText
import dev.deftu.textile.Text
import dev.deftu.textile.minecraft.MCText
import org.polyfrost.chatting.core.McChatLine
import org.polyfrost.chatting.core.WHITE
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.data.PolyImage
import kotlin.math.roundToInt

class ChatLineElement(
    val text: Text,
    val fullMessage: McChatLine,
    val hasHead: Boolean,
    val head: PolyImage?
) {

    val string = text.string

    var color = rgba(0, 0, 0, 0f).asMutable()

    var opacity = 0.0
        set(value) {
            field = value
            alpha = (255 * value).roundToInt()
        }

    var alpha = 0

    var renders = false

    fun render(ctx: OmniRenderingContext) {
        if (alpha <= 3) return
        val x = if (hasHead) 10f else 0f
        ctx.renderText(string, x, 0f, WHITE.withAlpha(alpha), true)
    }
}