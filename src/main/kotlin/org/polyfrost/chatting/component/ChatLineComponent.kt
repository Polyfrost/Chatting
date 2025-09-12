package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.util.math.ColorHelper
import org.polyfrost.chatting.mcScale
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.by
import kotlin.math.ceil
import kotlin.math.roundToInt

class ChatLineComponent(val visible: ChatHudLine.Visible, val fullMessage: ChatHudLine, val hasHead: Boolean = false): Drawable(size = (320 + 12) * mcScale by ceil(9 * mcScale)) {

    var index = -1

    var opacity = 1f

    init {
        color = rgba(0, 0, 0, 0.5f)
    }

    fun renderLegacy(matrixStack: OmniMatrixStack) {
        if (!renders) return
        val chatComponent = this.parent as Drawable
        val textY = chatComponent.y + (9 * index + 1) * mcScale * chatComponent.scaleY
        val alpha = (255 * opacity).roundToInt()

        if (alpha <= 3) return
        matrixStack.push()
        matrixStack.translate(x / mcScale + 4 * chatComponent.scaleX, textY / mcScale, 0f)
        matrixStack.scale(chatComponent.scaleX, chatComponent.scaleY, 1f)
        if (hasHead) {

            matrixStack.translate(10f, 0f, 0f)
        }
        OmniClient.fontRenderer.draw(visible.comp_896, 0f, 0f, ColorHelper.withAlpha(alpha, -1), true, matrixStack.toVanillaStack().peek().positionMatrix, mc.bufferBuilders.entityVertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
        matrixStack.pop()
    }

    override fun preRender(delta: Long) {
        size = (320 + 12) * mcScale by ceil(9 * mcScale)
        super.preRender(delta)
    }

    override fun render() {
        renderer.rect(x, y, width, height, rgba(0, 0, 0, 0.5f * opacity))
    }

}