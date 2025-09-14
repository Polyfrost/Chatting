package org.polyfrost.chatting.component

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.OrderedText
import net.minecraft.util.math.ColorHelper
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.chatting.mcScale
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.extensions.onHover
import org.polyfrost.polyui.component.extensions.onHoverExit
import org.polyfrost.polyui.unit.by
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

open class ChatLineComponent(val window: ChatWindow, val visible: ChatHudLine.Visible? = null, val fullMessage: ChatHudLine? = null, val hasHead: Boolean = false): Drawable(size = (320 + 12) * mcScale by ceil(9 * mcScale)) {

    var index = -1

    var opacity = 1f

    var mAlpha = 0

    var bgColor = window.bgColor.asMutable()

    var selected = false

    init {
        acceptsInput = true
        onHover {
            selected = true
            bgColor = window.bgColor_hovered.asMutable()
        }
        onHoverExit {
            selected = false
            bgColor = window.bgColor.asMutable()
        }
    }

    fun refreshColor() {
        bgColor = if (selected) {
            window.bgColor_hovered.asMutable()
        } else {
            window.bgColor.asMutable()
        }
    }

    fun renderLegacy(drawContext: DrawContext) {
        if (!renders) return
        val text = getText() ?: return
        val chatComponent = this.parent as Drawable

        if (mAlpha <= 3) return
        val matrices = drawContext.matrices
        matrices.push()
        matrices.translate(x / mcScale, y / mcScale, 0f)
        matrices.scale(chatComponent.scaleX, chatComponent.scaleY, 1f)
        if (hasHead) {
            matrices.translate(10f, 0f, 0f)
        }

        val indicator = visible?.indicator
        if (indicator != null) {
            val c = indicator.comp_899() or (mAlpha shl 24)
            drawContext.fill(0, 0, 2, 9, c)
        }

        drawContext.drawTextWithShadow(mc.textRenderer, text, 4, 1, getTextColor())

        matrices.pop()
    }

    open fun getText(): OrderedText? {
        return visible?.comp_896
    }

    open fun getTextColor(): Int {
        return ColorHelper.withAlpha(mAlpha, -1)
    }

    fun update(size: Boolean) {
        val parent = parent as ChatComponent
        at = parent.x by parent.y + parent.lineHeight * index
        if (size) {
            width = parent.width * parent.scaleX
            height = parent.lineHeight.toFloat()
        }
    }

    override fun render() {
        mAlpha = (opacity * 255).roundToInt()
        if (mAlpha <= 3) return
        val alphaTemp = bgColor.alpha
        bgColor.alpha *= opacity
        val parent = parent as ChatComponent
        val hudScale = min(parent.scaleX, parent.scaleY)
        val topRadius = if (this.index == 0) window.cornerRadius * hudScale else 0f
        val bottomRadius = if (this.index == window.length - 1) window.cornerRadius * hudScale else 0f
        val radii = floatArrayOf(topRadius, topRadius, bottomRadius, bottomRadius)
        renderer.rect(x, y, width, height, bgColor, radii)
        bgColor.alpha = alphaTemp
    }

}