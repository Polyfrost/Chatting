package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.util.math.ColorHelper
import org.polyfrost.chatting.legacyComponents
import org.polyfrost.chatting.mcScale
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.by
import kotlin.math.*

class ChatLineComponent(val visible: ChatHudLine.Visible, val fullMessage: ChatHudLine): Drawable(size = (320 + 12) * mcScale by ceil(9 * mcScale)) {

    var index = -1

    var opacity = 1f

    val textComponent = object : LegacyComponent() {
        override fun render(matrixStack: OmniMatrixStack) {
            if (this@ChatLineComponent._parent?._parent == null) {
                remove = true
                return
            }
            if (!this@ChatLineComponent.renders) return

            val chatComponent = this@ChatLineComponent.parent as Drawable
            val textY = chatComponent.y + (9 * index + 1) * mcScale * chatComponent.scaleY
            val alpha = (255 * opacity).roundToInt()

            if (alpha <= 3) return

            matrixStack.push()
            matrixStack.translate(chatComponent.x / mcScale + 4 * chatComponent.scaleY, textY / mcScale, 0f)
            matrixStack.scale(chatComponent.scaleX, chatComponent.scaleY, 1f)
            OmniClient.fontRenderer.draw(visible.comp_896, 0f, 0f, ColorHelper.withAlpha(alpha, -1), true, matrixStack.toVanillaStack().peek().positionMatrix, OmniClient.getInstance().bufferBuilders.entityVertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
            matrixStack.pop()
        }
    }

    init {
        color = rgba(0, 0, 0, 0.5f)
        legacyComponents.add(textComponent)
    }

    override fun preRender(delta: Long) {
        size = (320 + 12) * mcScale by ceil(9 * mcScale)
        super.preRender(delta)
    }

    override fun render() {
        val renderY = y + index * 9 * mcScale
        renderer.rect(x, renderY, width, height, rgba(0, 0, 0, 0.5f * opacity))
    }

}