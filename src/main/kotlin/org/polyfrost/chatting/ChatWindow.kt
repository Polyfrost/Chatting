package org.polyfrost.chatting

import net.minecraft.client.gui.screen.ChatScreen
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.ChatLineComponent
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.fastEach
import kotlin.math.min
import kotlin.math.pow

class ChatWindow(preview: Boolean = false) : Hud<Drawable>(id = "chat.yml", title = "Chat", category = Category.INFO) {

    @Color(
        title = "Background Color"
    )
    var bgColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Hovered Background Color"
    )
    var bgColor_hovered = rgba(255, 255, 255, 0.5f)

    @Slider(
        title = "Corner Radius",
        min = 0f,
        max = 10f,
    )
    var cornerRadius = 4f

    var isPreview = preview

    var indexShifting = 0

    var length = 0

    override fun addCallbacks(tree: Tree) {
        super.addCallbacks(tree)
        tree.getProp("bgColor")?.addCallback {
            refreshColor()
            false
        }
        tree.getProp("bgColor_hovered")?.addCallback {
            refreshColor()
            false
        }
    }

    fun refreshColor() {
        if (isPreview) return
        get().children!!.fastEach {
            (it as ChatLineComponent).refreshColor()
        }
    }

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
            var index = 0
            val inChatScreen = mc.currentScreen != null && mc.currentScreen is ChatScreen
            val pendingCount = mc.messageHandler.unprocessedMessageCount
            val hasPending = pendingCount > 0L && !HudManager.panelExists
            if (hasPending != this.hasPending) {
                handleDelay(hasPending)
            }
            length = children!!.count {
                val creationTick = (it as ChatLineComponent).visible?.comp_895 ?: -1
                val fullOpacity = inChatScreen || creationTick == -1
                (mc.inGameHud.ticks - creationTick) / 200f
                val canRender = fullOpacity || mc.inGameHud.ticks - creationTick <= 200
                if (it.selected && !inChatScreen && !HudManager.panelExists) {
                    it.selected = false
                    it.refreshColor()
                }
                it.renders = canRender
                if (canRender) {
                    it.opacity = if (fullOpacity) {
                        1f
                    } else {
                        Math.clamp(10 - (mc.inGameHud.ticks - creationTick) / 20f, 0f, 1f).pow(2)
                    }
                }
                return@count canRender
            }
            this.size = (320 + 12) * mcScale by min(length, 10) * 9 * mcScale
            indexShifting = if (length > 10) length - 10 else 0
            children!!.fastEach {
                if (it.renders) {
                    it as ChatLineComponent
                    val newIndex = index - indexShifting
                    if (newIndex < 0) {
                        it.renders = false
                    } else {
                        it.index = newIndex
                        it.update(true)
                    }
                    index++
                }
            }
        }

        return true
    }

    override fun hasBackground() = false

}