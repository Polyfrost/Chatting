@file:JvmName("Util")

package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.function.Function

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

private fun OmniMatrixStack.drawTexture(function: Function<Identifier, RenderLayer>, identifier: Identifier, x: Int, y: Int, width: Int, height: Int, u: Float, uWidth: Float, v: Float, vHeight: Float, texWidth: Float, texHeight: Float, color: Int) {
    val renderLayer = function.apply(identifier)
    val matrix4f = this.toVanillaStack().peek().positionMatrix
    val vertexConsumer = mc.bufferBuilders.entityVertexConsumers.getBuffer(renderLayer)
    val xEnd = (x + width).toFloat()
    val yEnd = (y + height).toFloat()
    val uStart = u / texWidth
    val vStart = v / texHeight
    val uEnd = (u + uWidth) / texWidth
    val vEnd = (v + vHeight) / texHeight
    vertexConsumer.vertex(matrix4f, x.toFloat(), y.toFloat(), 0.0F).texture(uStart, vStart).color(color)
    vertexConsumer.vertex(matrix4f, x.toFloat(), yEnd, 0.0F).texture(uStart, vEnd).color(color)
    vertexConsumer.vertex(matrix4f, xEnd, yEnd, 0.0F).texture(uEnd, vEnd).color(color)
    vertexConsumer.vertex(matrix4f, xEnd, y.toFloat(), 0.0F).texture(uEnd, vStart).color(color)
}