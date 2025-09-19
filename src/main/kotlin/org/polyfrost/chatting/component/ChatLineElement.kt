package org.polyfrost.chatting.component

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.util.math.ColorHelper
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.roundToInt

class ChatLineElement(val visible: ChatHudLine.Visible) {

    var opacity = 0.0

    var renders = false

    fun render(drawContext: DrawContext) {
        val alpha = (255 * opacity).roundToInt()
        if (alpha <= 3) return
        drawContext.drawTextWithShadow(mc.textRenderer, visible.comp_896, 0, 0, ColorHelper.withAlpha(alpha, -1))
    }
}