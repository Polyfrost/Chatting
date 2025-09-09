package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.ChatHudLine
import org.polyfrost.chatting.mcScale
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.by

class ChatLineComponent(val visible: ChatHudLine.Visible, val fullMessage: ChatHudLine, x: Float = 0f, y: Float = 0f): Drawable(at = x by y, size = (320 + 12) * mcScale by 9f * mcScale) {


    init {
        color = rgba(0, 0, 0, 0.5f)
    }

    override fun preRender(delta: Long) {
        size = (320 + 12) * mcScale by 9 * mcScale
        super.preRender(delta)
    }

    override fun render() {
        val index = parent.children!!.indexOf(this)
        val renderY = y + index * 9 * mcScale
        renderer.rect(x, renderY, width, height, color)

        val matrixStack = OmniMatrixStack()
        matrixStack.push()
        val textY = (parent as ChatComponent).actualY + 9 * index * mcScale * (parent as Drawable).scaleY
        matrixStack.translate((parent as ChatComponent).actualX / mcScale + 4, textY / mcScale + 1, 0f)
        matrixStack.scale((parent as Drawable).scaleX, (parent as Drawable).scaleY, 1f)
        OmniClient.fontRenderer.draw(visible.comp_896, 0f, 0f, -1, true, matrixStack.toVanillaStack().peek().positionMatrix, OmniClient.getInstance().bufferBuilders.entityVertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
        matrixStack.pop()
    }

}