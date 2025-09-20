package org.polyfrost.chatting.component

import dev.deftu.omnicore.client.OmniKeyboard
import dev.deftu.omnicore.client.OmniMouse
import dev.deftu.omnicore.client.render.OmniMatrixStack
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.chatting.animation.DummyAnimation
import org.polyfrost.chatting.copyToClipboard
import org.polyfrost.chatting.editorMessages
import org.polyfrost.chatting.event.MouseActionEvent
import org.polyfrost.chatting.event.NewMessageEvent
import org.polyfrost.chatting.mcScale
import org.polyfrost.chatting.mixin.ChatHudAccessor
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.hud.v1.events.HudEditorToggleEvent
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Linear
import org.polyfrost.polyui.color.argb
import org.polyfrost.polyui.color.asMutable
import org.polyfrost.polyui.component.Component
import org.polyfrost.polyui.component.extensions.onHover
import org.polyfrost.polyui.component.extensions.onHoverExit
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.ms
import kotlin.math.*

class ChatComponent(val window: ChatWindow) : LegacyHud.LegacyHudComponent(window, null, size = 10f by 10f) {

    var elements = ArrayList<ChatLineElement>()

    var selectedElements = ArrayList<Int>()

    var currentHovered = -1

    var lastSelected = -1

    var hovered = false

    var lineHeight = 0

    var scrollAmount: Animation = DummyAnimation(0f)

    var translateAmount = 0f

    var drawingStart = 0

    var drawingEnd = 0

    init {
        eventHandler { event: NewMessageEvent ->
            if (HudManager.isEditing) return@eventHandler
            addMessage(event.message, true)
        }
        eventHandler { event: HudEditorToggleEvent ->
            swap(event.open)
        }
        eventHandler { event: MouseInputEvent.Moved ->
            if (!hovered) return@eventHandler
            getCurrentElement(event.x, event.y)
        }
        eventHandler { event: MouseActionEvent.Companion.Click ->
            if (event.mouseOver == null) {
                selectedElements.clear()
            } else {
                if (event.mouseOver == this) {
                    when (event.button) {
                        0 -> leftClick()
                        1 -> rightClick()
                    }
                }
            }
        }
        onHover {
            getCurrentElement()
            hovered = true
        }
        onHoverExit {
            hoverExit()
        }
    }

    fun leftClick() {
        if (currentHovered == -1) return
        if (OmniKeyboard.isShiftKeyPressed) {
            selectedElements.clear()
            val currentIndex = currentHovered
            val lastIndex = if (lastSelected == -1) 0 else lastSelected
            for (i in min(lastIndex, currentIndex)..max(lastIndex, currentIndex)) {
                selectedElements.add(i)
            }
        } else {
            if (!OmniKeyboard.isCtrlKeyPressed || !OmniKeyboard.isAltKeyPressed) {
                selectedElements.clear()
            }
            selectedElements.add(currentHovered)
            lastSelected = currentHovered
        }
    }

    fun rightClick() {
        if (selectedElements.isEmpty()) {
            if (currentHovered == -1) return
            elements[currentHovered].message.copyToClipboard()
        } else {
            val stringBuilder = StringBuilder()
            for (index in selectedElements) {
                stringBuilder.append(elements[index].message)
                if (index != selectedElements.last()) {
                    stringBuilder.append("\n")
                }
            }
            stringBuilder.toString().copyToClipboard()
            selectedElements.clear()
        }
    }

    fun scroll(amount: Float) {
        val maxAmount = if (elements.size <= window.lineLimit) {
            0f
        } else {
            (elements.size - window.lineLimit).toFloat()
        }
        val target = (scrollAmount.to + amount).coerceIn(0f..maxAmount)
        if (target == scrollAmount.to) return
        if (window.smoothScrolling) {
            val distance = abs(target - scrollAmount.value)
            scrollAmount = Linear((50 * distance).ms, scrollAmount.value, target)
        } else {
            scrollAmount = DummyAnimation(target)
        }
        getCurrentElement()
    }

    fun hoverExit() {
        hovered = false
        currentHovered = -1
    }

    fun getCurrentElement(mouseX: Float = OmniMouse.rawX.toFloat(), mouseY: Float = OmniMouse.rawY.toFloat()) {
        if (elements.isEmpty()) return
        val index = elements.size - 1 - floor((y + height * scaleY + lineHeight * scrollAmount.value - mouseY) / lineHeight).toInt()
        if (index < 0 || index >= elements.size) return
        currentHovered = index
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
            if (HudManager.isEditing) {
                addExampleText()
            } else {
                addAllMessages()
            }
        }
    }

    fun drawLegacy(stack: OmniMatrixStack) {
        if (elements.isEmpty()) return
        stack.push()
        stack.translate(x / mcScale, y / mcScale, 0f)
        stack.scale(scaleX, scaleY, 1f)
        stack.translate(0f, -translateAmount * 9, 1f)
        stack.translate(4f, 1f, 0f)
        for ((index, element) in elements.withIndex()) {
            if (!element.renders) continue
            if (index !in drawingStart..drawingEnd) continue
            element.render(stack)
            stack.translate(0f, 9f, 0f)
        }
        stack.pop()
    }

    override var width = (this as Component).width

    override var height = (this as Component).height

    override fun preRender(delta: Long) {
        if (hovered && !scrollAmount.isFinished) getCurrentElement()
        val scroll = scrollAmount.update(delta)
        drawingStart = if (elements.size <= window.lineLimit) {
            0
        } else {
            elements.size - window.lineLimit - ceil(scroll).toInt()
        }
        val full = scroll.toInt().toFloat() == scroll
        drawingEnd = drawingStart + window.lineLimit + if (full) -1 else 0
        translateAmount = if (full) 0f else 1f - scroll + floor(scroll)

        val tempScaleX = scaleX
        val tempScaleY = scaleY
        x = round(x)
        y = round(y)
        scaleX = 1f
        scaleY = 1f
        super.preRender(delta)
        scaleX = tempScaleX
        scaleY = tempScaleY
    }

    override fun render() {
        renderer.pushScissor(x, y, width * scaleX, height * scaleY)
        renderer.translate(0f, -translateAmount * lineHeight)
        for ((index, element) in elements.withIndex()) {
            if (!element.renders) continue
            if (index !in drawingStart..drawingEnd) continue
            val color = when ((index)) {
                in selectedElements -> {
                    window.bgColorSelected
                }
                currentHovered -> {
                    window.bgcolorHovered
                }
                else -> {
                    window.bgColor
                }
            }.asMutable()
            renderer.rect(x, y, width * scaleX, lineHeight.toFloat(), color)
            val indicator = element.visible.indicator
            if (indicator != null) {
                val ac = indicator.comp_899 or (element.alpha shl 24)
                renderer.rect(x, y, 2 * mcScale * scaleX, lineHeight.toFloat(), argb(ac))
            }
            renderer.translate(0f, lineHeight.toFloat())
        }
        renderer.popScissor()
    }
}