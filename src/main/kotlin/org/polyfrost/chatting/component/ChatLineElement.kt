package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import org.polyfrost.chatting.McChatLine
import org.polyfrost.chatting.WHITE
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.data.PolyImage
import kotlin.math.roundToInt

class ChatLineElement(
    val text:
    //#if MC >= 1.16.5
    net.minecraft.text.OrderedText,
    private val str: String,
    //#else
    //$$ net.minecraft.text.Text,
    //#endif
    val fullMessage: McChatLine,
    val hasHead: Boolean,
    val head: PolyImage?
) {

    val string: String
        get() {
            //#if MC >= 1.16.5
            return str
            //#else
            //$$ return text.asFormattedString()
            //#endif
        }

    var color = rgba(0, 0, 0, 0f).asMutable()

    var opacity = 0.0
        get() = field
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