package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import net.minecraft.client.gui.hud.ChatHudLine
import org.polyfrost.chatting.WHITE
import org.polyfrost.chatting.asString
import org.polyfrost.polyui.data.PolyImage
import kotlin.math.roundToInt

class ChatLineElement(val visible: ChatHudLine.Visible, val hasHead: Boolean, val head: PolyImage?) {

    val message = visible.comp_896.asString()

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
        ctx.renderText(message, x, 0f, WHITE.withAlpha(alpha), true)
    }
}