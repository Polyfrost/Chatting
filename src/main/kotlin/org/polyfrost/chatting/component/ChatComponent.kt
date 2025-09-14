package org.polyfrost.chatting.component

import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.chatting.event.NewMessageEvent
import org.polyfrost.chatting.mcScale
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
import kotlin.math.round
import kotlin.math.roundToInt

class ChatComponent(val window: ChatWindow) : Drawable(null, size = 1f by 1f) {

    @Transient
    val editorMessages = mutableListOf(
        "§bChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    )

    var lastX = -1f

    var lastY = -1f

    var lastScaleX = -1f

    var lastScaleY = -1f

    var lineHeight = 0

    var hasPending = false

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

    fun handleDelay(pending: Boolean) {
        if (pending) {
            addChild(ChatPendingComponent(window))
        } else {
            removeChild(children!!.size - 1)
        }
        hasPending = pending
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
            val line = ChatHudLine(-1, Text.literal(message), null, null)
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
        hasPending = false
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

        val pendingComponent = if (hasPending) children!!.last() else null

        pendingComponent?.let { removeChild(it, recalculate = false) }

        lines.forEach {
            val component = ChatLineComponent(window, ChatHudLine.Visible(message.creationTick(), it, message.indicator(), it == lines.last()), message, hasHead)
            addChild(component, recalculate = false)
            while(children!!.size > 100) {
                removeChild(0, recalculate = false)
            }
        }

        pendingComponent?.let { addChild(it, recalculate = false) }

        window.update()
    }

    override var renders: Boolean
        get() = super.renders && !children.isNullOrEmpty()
        set(value) {
            super.renders = value
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
        var updateSize = false
        var update = false
        if (x != lastX || y != lastY) {
            x = round(x)
            y = round(y)
            lastX = x
            lastY = y
            update = true
        }
        if (scaleX != lastScaleX || scaleY != lastScaleY) {
            lastScaleX = scaleX
            lastScaleY = scaleY
            lineHeight = (9 * mcScale * scaleY).roundToInt()
            update = true
            updateSize = true
        }
        if (update) {
            children!!.fastEach {
                (it as ChatLineComponent).update(updateSize)
            }
        }
        scaleX = 1f
        scaleY = 1f
        super.preRender(delta)
        scaleX = lastScaleX
        scaleY = lastScaleY
    }

    override fun render() {
    }
}