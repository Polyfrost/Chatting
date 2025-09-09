package org.polyfrost.chatting

import net.minecraft.client.gui.screen.ChatScreen
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.ChatLineComponent
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.pow

class ChatWindow(preview: Boolean = false) : Hud<Drawable>(id = "chat.yml", title = "Chat", category = Category.INFO) {

    var isPreview = preview

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
        get().width = (320 + 12) * mcScale
        var index = 0
        val inChatScreen = mc.currentScreen != null && mc.currentScreen is ChatScreen
        val size = get().children!!.count {
            val creationTick = (it as ChatLineComponent).visible.comp_895
            val fullOpacity = inChatScreen || creationTick == -1
             (mc.inGameHud.ticks - creationTick) / 200f
            val canRender = fullOpacity || mc.inGameHud.ticks - creationTick <= 200
            it.renders = canRender
            if (canRender) {
                it.opacity = if (fullOpacity) {
                    1f
                } else {
                    Math.clamp(10 - (mc.inGameHud.ticks - creationTick) / 20f, 0f, 1f).pow(2)
                }
                it.index = index
                index++
            }
            return@count canRender
        }
        get().height = size * 9 * mcScale
        return true
    }

    override fun hasBackground() = false

}