package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.omnicore.api.client.screen.isInChatScreen
import dev.deftu.omnicore.api.client.screen.isInScreen
import org.polyfrost.chatting.core.mcScale
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class ChatWindow(preview: Boolean = false) : LegacyHud(id = "chat.yml", title = "Chat", category = Category.INFO) {

    @Color(
        title = "Background Color"
    )
    var bgColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Hovered Background Color"
    )
    var bgcolorHovered = rgba(255, 255, 255, 0.5f)

    @Color(
        title = "Selected Background Color"
    )
    var bgColorSelected = rgba(255, 255, 255, 0.75f)

    @Slider(
        title = "Corner Radius",
        min = 0f,
        max = 10f,
    )
    var cornerRadius = 0f

    @Switch(
        title = "Smooth Scrolling"
    )
    var smoothScrolling = false

    var lineLimit = 10

    var isPreview = preview

    var length = 0

    override fun clone(): Hud<Drawable> {
        return (super.clone() as ChatWindow).apply {
            isPreview = false
        }
    }

    override fun create(): Drawable {
        return if (isPreview) Text("Chat", fontSize = 32f) else ChatComponent(this)
    }

    override fun updateFrequency(): Long {
        return 25.milliseconds
    }

    override var width = 0f

    override var height = 0f

    override fun renderLegacy(ctx: OmniRenderingContext, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        (get() as ChatComponent).drawLegacy(ctx)
    }

    override fun update(): Boolean {
        if (get() is Text) return false
        with(get() as ChatComponent) {
            val inChat = isInScreen && isInChatScreen
            length = elements.count {
                val creationTick = it.fullMessage.creationTick
                val currentTick = mc.inGameHud.ticks
                val fullOpacity = inChat || creationTick == -1
                (currentTick - creationTick) / 200f
                val canRender = fullOpacity || currentTick - creationTick <= 200
                it.renders = canRender
                if (canRender) {
                    it.opacity = if (fullOpacity) {
                        1.0
                    } else {
                        val opacity = (10 - (currentTick - creationTick) / 20.0)
                        min(max(opacity, 0.0), 1.0).pow(2)
                    }
                }
                return@count canRender
            }
            lineHeight = (9 * mcScale * scaleY).roundToInt()
            this.size = (320 + 12) * mcScale by min(length, lineLimit) * 9 * mcScale
        }
        return true
    }

    override fun hasBackground() = false

}