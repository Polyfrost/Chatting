package org.polyfrost.chatting.component

import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.util.ChatMessages
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.chatting.event.NewMessageEvent
import org.polyfrost.chatting.mixin.ChatHudAccessor
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.events.HudEditorToggleEvent
import org.polyfrost.oneconfig.api.ui.v1.UIManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.utils.fastEach
import kotlin.math.floor

class ChatComponent(val window: ChatWindow) : Drawable(null, size = 1f by 1f) {

    @Transient
    val editorMessages = mutableListOf(
        "§bChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    )

    init {
        eventHandler { event: NewMessageEvent ->
            addMessage(event.message)
        }
        eventHandler { event: HudEditorToggleEvent ->
            swap(event.open)
        }
        eventHandler { event: MouseInputEvent.Moved ->
            if (mc.currentScreen != null && mc.currentScreen is ChatScreen) {
                UIManager.INSTANCE.defaultInstance.inputManager.mouseMoved(event.x, event.y)
            }
        }
    }

    fun swap(editing: Boolean) {
        removeAllMessages()
        if (editing) {
            addExampleText()
        } else {
            addAllMessages()
        }
    }

    fun addExampleText() {
        editorMessages.forEach { message ->
            val line = ChatHudLine(-1, net.minecraft.text.Text.literal(message), null, null)
            addMessage(line)
        }
    }

    fun addAllMessages() {
        (mc.inGameHud.chatHud as ChatHudAccessor).messages.reversed().forEach {
            addMessage(it)
        }
    }

    fun removeAllMessages() {
        if (children!!.isEmpty()) return
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

        val hasHead = false

        if (hasHead) {
            i -= 10
        }

        val lines = ChatMessages.breakRenderedChatMessageLines(message.comp_893, i, mc.textRenderer)

        lines.forEach {
            val component = ChatLineComponent(ChatHudLine.Visible(message.creationTick(), it, message.indicator(), it == lines.last()), message, hasHead)
            addChild(component, recalculate = false)
            while(children!!.size > 100) {
                removeChild(0)
            }
        }
        window.update()
    }

    override fun setup(polyUI: PolyUI): Boolean {
        return super.setup(polyUI).also {
            if (HudManager.panelExists) {
                addExampleText()
            } else {
                addAllMessages()
            }
        }
    }

    override fun preRender(delta: Long) {
        children!!.fastEach {
            (it as Drawable).scaleX = scaleX
            (it as Drawable).scaleY = scaleY
        }
    }

    override var renders: Boolean
        get() = super.renders && !children.isNullOrEmpty()
        set(value) {
            super.renders = value
        }

    override fun render() {
    }
}