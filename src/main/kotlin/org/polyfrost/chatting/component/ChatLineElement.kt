package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.render.OmniGameRendering
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.util.math.ColorHelper
import org.polyfrost.chatting.asString
import kotlin.math.roundToInt

class ChatLineElement(val visible: ChatHudLine.Visible) {

    val message = visible.comp_896.asString()

    var opacity = 0.0
        set(value) {
            field = value
            alpha = (255 * value).roundToInt()
        }

    var alpha = 0

    var renders = false

    fun render(stack: OmniMatrixStack) {
        if (alpha <= 3) return
        OmniGameRendering.drawText(stack, message, 0f, 0f, ColorHelper.withAlpha(alpha, -1), true)
    }
}