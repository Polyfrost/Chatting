package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import org.polyfrost.chatting.WHITE
import org.polyfrost.chatting.util.MessageInfo
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.data.PolyImage
import kotlin.math.roundToInt

class ChatLineElement(val messageInfo: MessageInfo, val hasHead: Boolean, val head: PolyImage?) {

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
        ctx.renderText(messageInfo.string, x, 0f, WHITE.withAlpha(alpha), true)
    }
}