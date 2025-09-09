package org.polyfrost.chatting.component

import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.util.ChatMessages
import org.polyfrost.chatting.event.HudEditorEvent
import org.polyfrost.chatting.event.NewMessageEvent
import org.polyfrost.chatting.mixin.ChatHudAccessor
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.color.PolyColor
import org.polyfrost.polyui.component.impl.Block
import org.polyfrost.polyui.unit.by

class ChatComponent : Block(null, color = PolyColor.TRANSPARENT, size = 320f by 32f) {

    @Transient
    val editorMessages = mutableListOf(
        "§bChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    )

    @Transient
    var actualX = 0f

    @Transient
    var actualY = 0f

    init {
        eventHandler { event: NewMessageEvent ->
            addMessage(event.message)
        }
        eventHandler { event: HudEditorEvent ->
            swap(event.state)
        }
    }

    fun swap(editing: Boolean) {
        removeAllMessages()
        if (editing) {
            editorMessages.forEach { message ->
                val line = ChatHudLine(-1, net.minecraft.text.Text.literal(message), null, null)
                addMessage(line)
            }
        } else {
            addAllMessages()
        }
    }

    fun addAllMessages() {
        (mc.inGameHud.chatHud as ChatHudAccessor).messages.forEach {
            addMessage(it)
        }
    }

    fun removeAllMessages() {
        for (i in 0..<children!!.size) {
            removeChild(0, false)
        }
    }

    fun addMessage(message: ChatHudLine) {
        var i = 320
        val icon = message.icon
        if (icon != null) {
            i -= icon.width + 4 + 2
        }
        val lines = ChatMessages.breakRenderedChatMessageLines(message.comp_893(), i, mc.textRenderer)

        lines.forEach {
            val component = ChatLineComponent(ChatHudLine.Visible(message.creationTick(), it, message.indicator(), it == lines.last()), message)
            addChild(component, recalculate = false)
            while(children!!.size > 100) {
                removeChild(0)
            }
        }

    }

    override fun setup(polyUI: PolyUI): Boolean {
        return super.setup(polyUI).also {
            addAllMessages()
        }
    }

    override fun preRender(delta: Long) {
        actualX = x
        actualY = y
        super.preRender(delta)
    }
}