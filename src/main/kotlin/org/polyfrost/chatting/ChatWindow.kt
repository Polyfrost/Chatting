package org.polyfrost.chatting

import dev.deftu.omnicore.client.OmniScreen
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.pow

class ChatWindow(preview: Boolean = false) : Hud<Drawable>(id = "chat.yml", title = "Chat", category = Category.INFO) {

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

    var isPreview = preview

    var length = 0

    override fun clone(): Hud<Drawable> {
        return (super.clone() as ChatWindow).apply { isPreview = false }
    }

    override fun create(): Drawable {
        return if (isPreview) Text("Chat", fontSize = 32f) else ChatComponent(this)
    }

    override fun updateFrequency(): Long {
        return 25.milliseconds
    }

    override fun update(): Boolean {
        if (get() is Text) return false
        with(get() as ChatComponent) {
            val inChat = OmniScreen.isInScreen && OmniScreen.isInChat
            length = elements.count {
                val creationTick = it.visible.comp_895
                val fullOpacity = inChat || creationTick == -1
                (mc.inGameHud.ticks - creationTick) / 200f
                val canRender = fullOpacity || mc.inGameHud.ticks - creationTick <= 200
                it.renders = canRender
                if (canRender) {
                    it.opacity = if (fullOpacity) {
                        1.0
                    } else {
                        Math.clamp(10 - (mc.inGameHud.ticks - creationTick) / 20.0, 0.0, 1.0).pow(2)
                    }
                }
                return@count canRender
            }
            this.size = (320 + 12) * mcScale by length * 9 * mcScale
        }

        return true
    }

    override fun hasBackground() = false

}