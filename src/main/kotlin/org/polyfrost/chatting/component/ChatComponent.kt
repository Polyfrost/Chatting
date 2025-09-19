package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.OmniMouse
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.ChatHudLine
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
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.extensions.onClick
import org.polyfrost.polyui.component.extensions.onHover
import org.polyfrost.polyui.component.extensions.onHoverExit
import org.polyfrost.polyui.unit.by
import kotlin.math.floor

class ChatComponent(val window: ChatWindow) : Drawable(null, size = 1f by 1f) {

    @Transient
    val editorMessages = mutableListOf(
        "§bChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    )

    var elements = ArrayList<ChatLineElement>()

    var selectedElements = ArrayList<ChatLineElement>()

    var currentHovered: ChatLineElement? = null

    var hovered = false

    init {
        eventHandler { event: NewMessageEvent ->
            if (HudManager.panelExists) return@eventHandler
            addMessage(event.message, true)
        }
        eventHandler { event: HudEditorToggleEvent ->
            swap(event.open)
        }
        eventHandler { event: MouseInputEvent.Moved ->
            if (!hovered) return@eventHandler
            getCurrentElement(event.x, event.y)
        }
        onHover {
            getCurrentElement()
            hovered = true
        }
        onHoverExit {
            hoverExit()
        }
        onClick {
            if (currentHovered == null) return@onClick
            println("select $currentHovered")
            selectedElements.add(currentHovered!!)
        }
    }

    fun hoverExit() {
        hovered = false
        currentHovered = null
    }

    fun getCurrentElement(mouseX: Float = OmniMouse.rawX.toFloat(), mouseY: Float = OmniMouse.rawY.toFloat()) {
        if (elements.isEmpty()) return
        val index = floor((mouseY - y) / (9 * mcScale * scaleY)).toInt()
        if (index < elements.size - 1) return
        currentHovered = elements[index]
    }

    fun swap(editing: Boolean) {
        removeAllMessages(false)
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
        window.update()
    }

    fun addAllMessages() {
        (mc.inGameHud.chatHud as ChatHudAccessor).messages.reversed().forEach {
            addMessage(it)
        }
        window.update()
    }

    fun removeAllMessages(update: Boolean) {
        elements.clear()
        if (update) {
            window.update()
        }
    }

    fun addMessage(message: ChatHudLine, update: Boolean = false) {
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
            val visible = ChatHudLine.Visible(message.creationTick(), it, message.indicator(), it == lines.last())
            val element = ChatLineElement(visible)
            elements.add(element)
        }

        if (update) window.update()
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

    fun renderLegacy(drawContext: DrawContext) {
        if (elements.isEmpty()) return
        val matrices = drawContext.matrices
        matrices.push()
        matrices.translate(x / mcScale, y / mcScale, 0f)
        matrices.scale(scaleX, scaleY, 1f)
        matrices.translate(4f, 1f, 0f)
        for (element in elements) {
            if (!element.renders) continue
            element.render(drawContext)
            matrices.translate(0f, 9f, 0f)
        }
        matrices.pop()
    }

    override fun render() {
        val radius = window.cornerRadius * (scaleX + scaleY) / 2f
        val lineHeight = 9 * mcScale

        if (radius > 0f) {
            renderer.rect(x, y, width, height, window.bgColor, radius)
        } else {
            for (element in elements) {
                if (!element.renders) continue
                val color = when (element) {
                    currentHovered -> {
                        window.bgcolorHovered
                    }
                    in selectedElements -> {
                        window.bgColorSelected
                    }
                    else -> {
                        window.bgColor
                    }
                }
                renderer.rect(x, y, width, lineHeight, color)
                renderer.translate(0f, lineHeight)
            }
        }
    }
}