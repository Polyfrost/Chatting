package org.polyfrost.chatting

import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.ChatLineComponent
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.unit.milliseconds

class ChatWindow(preview: Boolean) : Hud<Drawable>(id = "chat.yml", title = "Chat", category = Category.INFO) {

    var isPreview = preview

    override fun clone(): Hud<Drawable> {
        return (super.clone() as ChatWindow).apply { isPreview = false }
    }

    override fun create(): Drawable {
        return if (isPreview) Text("Chat", fontSize = 32f) else ChatComponent()
    }

    override fun updateFrequency(): Long {
        return 25.milliseconds
    }

    override fun update(): Boolean {
        if (get() is Text) return false
        get().width = (320 + 12) * mcScale
        val size = get().children!!.count {
            val creationTick = (it as ChatLineComponent).visible.comp_895
            val canRender = inChatScreen || creationTick == -1 || creationTick >= 200
            it.renders = canRender
            return@count canRender
        }
        get().height = size * 9 * mcScale
        return true
    }

    override fun hasBackground() = false

}