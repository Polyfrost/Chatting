package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.network.message.ChatVisibility
import net.minecraft.util.math.ColorHelper
import org.polyfrost.chatting.mixin.ChatHudAccessor
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.OneImage
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.argb
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.milliseconds
import java.util.*
import javax.swing.text.StyleConstants.getComponent
import kotlin.math.round

class ChatWindow() : Hud<Drawable>() {

    override fun title(): String {
        return "Chat"
    }

    override fun category(): Category {
        return Category.INFO
    }

    override fun create(): Drawable {
        return ChatComponent()
    }

    override fun updateFrequency(): Long {
        return 25.milliseconds
    }

    override fun multipleInstancesAllowed() = false

    override fun defaultPosition(): Vec2 {
        return Vec2(200f, 200f)
    }

    override fun update(): Boolean {
        if (get() !is ChatComponent) return false
        val accessor = mc.inGameHud.chatHud as ChatHudAccessor
        hidden = mc.options.chatVisibility.getValue() == ChatVisibility.HIDDEN && accessor.visibleMessages.isEmpty()

        val bl = mc.currentScreen is ChatScreen
        var u = 0
        val d = mc.options.chatOpacity.getValue().toDouble() * 0.9 + 0.1
        val component = get() as ChatComponent
        component.renderingLines.clear()
        while (u + accessor.scrolledLines < accessor.visibleMessages.size && u < mc.inGameHud.chatHud.visibleLineCount) {
            val v = u + accessor.scrolledLines
            val visible = accessor.visibleMessages[v]
            if (visible != null) {
                val w = mc.inGameHud.ticks - visible.comp_895()
                if (w < 200 || bl) {
                    val h = if (bl) 1.0 else accessor.invokeGetMessageOpacityMultiplier(w)
                    val opacity = 255.0 * h
                    if ((opacity * d).toInt() > 3) {
                        component.renderingLines[visible] = opacity
                    }
                }
            }
            ++u
        }

        return true
    }

    override fun hasBackground() = false

    class ChatComponent() : Drawable() {

        override var width = 0f

        override var height = 0f

        var renderingLines = LinkedHashMap<ChatHudLine.Visible, Double>()

        override fun draw() { // height might equal 0
            val accessor = mc.inGameHud.chatHud as ChatHudAccessor
            width = (mc.inGameHud.chatHud.width + 12) * OmniResolution.scaleFactor.toFloat()
            height = renderingLines.size * accessor.invokeGetLineHeight() * OmniResolution.scaleFactor.toFloat()
            super.draw()
        }

        override fun render() {
            renderer.rect(x, y, width, height, rgba(0, 0, 0, 0.5f))
            val accessor = mc.inGameHud.chatHud as ChatHudAccessor
            val drawContext = DrawContext(mc, mc.bufferBuilders.entityVertexConsumers)
            val lineHeight = accessor.invokeGetLineHeight()
            val spacing = mc.options.chatLineSpacing.getValue().toDouble()
            val mcScale = OmniResolution.scaleFactor.toFloat()
            val chatScale = mc.inGameHud.chatHud.chatScale.toFloat()
            val chatOpacity = mc.options.chatOpacity.getValue() * 0.9 + 0.1
            val mcX = x / mcScale
            val mcY = y / mcScale
            renderingLines.reversed().onEachIndexed { i, (visible, opacity) ->
                val textOpacity = (opacity * chatOpacity).toInt()
                val textX = 4
                val lineY = i * lineHeight
                val textY = round(lineY + spacing * 5 + 1).toInt() // trust me
                val messageIndicator = visible.indicator()
                if (messageIndicator != null) {
                    val color = messageIndicator.comp_899() or (textOpacity shl 24)
                    renderer.rect(x, y + lineY.toFloat() * mcScale, 2 * mcScale, lineHeight * mcScale, argb(color))
                    renderer.
//                    drawContext.fill(0, lineY, 2, lineY + lineHeight, color)
                }
                drawContext.matrices.push()
                drawContext.matrices.translate(mcX, mcY, 0f)
                drawContext.matrices.scale(chatScale, chatScale, 1f)
                drawContext.drawTextWithShadow(mc.textRenderer, visible.comp_896(), textX, textY, ColorHelper.withAlpha(textOpacity, -1))
                drawContext.matrices.pop()
            }
        }

    }
}