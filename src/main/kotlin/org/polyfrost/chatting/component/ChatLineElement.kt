package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.textile.Text
import org.polyfrost.chatting.core.McChatLine
import org.polyfrost.chatting.core.WHITE
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.color.rgba
import kotlin.math.roundToInt

class ChatLineElement(
    val text: Text,
    val fullMessage: McChatLine,
    val hasHead: Boolean,
    val head: PlayerHead?
) {

    val lineOffset = if (hasHead) 10f else 0f

    val string = text.string

    var color = rgba(0, 0, 0, 0f).asMutable()

    var opacity = 0.0
        set(value) {
            field = value
            alpha = (255 * value).roundToInt()
            if (alpha <= 3) renders = false
        }

    var alpha = 0

    var renders = false

}