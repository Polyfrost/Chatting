package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.textile.minecraft.MCSimpleTextHolder
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.chatting.animation.DummyAnimation
import org.polyfrost.chatting.copyToClipboard
import org.polyfrost.chatting.editorMessages
import org.polyfrost.chatting.event.MouseActionEvent
import org.polyfrost.chatting.event.MessageEvent
import org.polyfrost.chatting.mcScale
import org.polyfrost.chatting.util.McChat
import org.polyfrost.chatting.util.MessageInfo
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.ScreenOpenEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.hud.v1.events.HudEditorToggleEvent
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Linear
import org.polyfrost.polyui.color.argb
import org.polyfrost.polyui.component.extensions.onHover
import org.polyfrost.polyui.component.extensions.onHoverExit
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.ms
import kotlin.math.*

class ChatComponent(val window: ChatWindow) : LegacyHud.LegacyHudComponent(window, size = 100f by 100f) {

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
        eventHandler { event: MessageEvent.Add ->
            if (HudManager.isEditing) return@eventHandler
            addMessage(event.messageInfo, true)
        }
        eventHandler { event: MessageEvent.Remove ->
            if (HudManager.isEditing) return@eventHandler
            removeMessage(event.messageInfo, true)
        }
        eventHandler { event: MessageEvent.Clear ->
            if (HudManager.isEditing) return@eventHandler
            removeAllMessages(true)
        }
        eventHandler { event: HudEditorToggleEvent ->
            swap(event.open)
        }
        eventHandler { event: MouseInputEvent.Moved ->
            if (!hovered) return@eventHandler
            getCurrentElement(event.y)
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
        eventHandler { event: ScreenOpenEvent ->
            if (event.getScreen<Any>() == null) {
                onClose()
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

    fun onClose() {
        hoverExit()
        selectedElements.clear()
        scrollAmount = DummyAnimation(0f)
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
            elements[currentHovered].messageInfo.string.copyToClipboard()
        } else {
            val stringBuilder = StringBuilder()
            for (index in selectedElements) {
                stringBuilder.append(elements[index].messageInfo.string)
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
        val target = round(scrollAmount.value + amount * if (OmniKeyboard.isShiftKeyPressed) 7f else 1f).coerceIn(0f..maxAmount)
        if (target == scrollAmount.value) return
        if (window.smoothScrolling) {
            val distance = abs(target - scrollAmount.value)
            scrollAmount = Linear((75 * distance).ms, scrollAmount.value, target)
        } else {
            scrollAmount = DummyAnimation(target)
        }
        getCurrentElement()
    }

    fun hoverExit() {
        hovered = false
        currentHovered = -1
    }

    fun getCurrentElement(mouseY: Float = OmniMouse.rawY.toFloat()) {
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
            val line = MessageInfo(-1, MCSimpleTextHolder(message), -1, null)
            addMessage(line)
        }
        window.update()
    }

    fun addAllMessages() {
        McChat.messages.values.reversed().forEach {
            addMessage(it)
        }
        window.update()
    }

    fun removeAllMessages(update: Boolean) {
        elements.clear()
        if (update) window.update()
    }

    fun addMessage(messageInfo: MessageInfo, update: Boolean = false) {
//        var i = 320
//        val icon = message.icon
//        if (icon != null) {
//            i -= icon.width + 4 + 2
//        }
//        val head = (message as ChatLineHook).`chatting$getHead`()
//        val head = null
//        val hasHead = head != null
//        if (hasHead) {
//            i -= 10
//        }

//        val lines = ChatMessages.breakRenderedChatMessageLines(messageInfo.text.asVanilla(), i, mc.textRenderer)
//        lines.forEach {
//            OmniTextRenderer.width()
//            val info = MessageInfo()
////            val visible = ChatHudLine.Visible(messageInfo.creationTick, it, message.indicator(), it == lines.last())
//            val element = ChatLineElement(visible, hasHead && it == lines.first(), head)
//            elements.add(element)
//        }
        val element = ChatLineElement(messageInfo, false, null)
        elements.add(element)

        if (update) window.update()
    }

    fun removeMessage(messageInfo: MessageInfo, update: Boolean = false) {
        elements.removeIf { it.equals(messageInfo) }
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

    override var width = 1f

    override var height = 1f

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
//        renderer.pushScissor(x, y, width * scaleX, height * scaleY)
        renderer.translate(x, y - translateAmount * lineHeight)
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
            }
            element.color.recolor(color)
            element.color.alpha *= element.opacity.toFloat()
            renderer.rect(0f, 0f, width * scaleX, lineHeight.toFloat(), element.color)
            if (element.hasHead) {
                renderer.image(element.head!!, 4 * mcScale, 1 * mcScale, 8f * mcScale * scaleX, 8f * mcScale * scaleY, colorMask = 0xFFFFFF or (element.alpha shl 24))
            }
            //#if MC > 1.16.5
            element.messageInfo.indicator?.let { indicator ->
                val ac = (indicator as net.minecraft.client.gui.hud.MessageIndicator).comp_899 or (element.alpha shl 24)
                renderer.rect(0f, 0f, 2 * mcScale * scaleX, lineHeight.toFloat(), argb(ac))
            }
            //#endif
            renderer.translate(0f, lineHeight.toFloat())
        }
//        renderer.popScissor()
    }

    fun drawLegacy(ctx: OmniRenderingContext) {
        if (elements.isEmpty()) return
        val matrices = ctx.matrices
        matrices.push()
        matrices.translate(x / mcScale, y / mcScale, 0f)
        matrices.scale(scaleX, scaleY, 1f)
        matrices.translate(4f, -translateAmount * 9 + 1f, 0f)
        ctx.pushScissor(0, 0, 5, 5)
        for ((index, element) in elements.withIndex()) {
            if (!element.renders) continue
            if (index !in drawingStart..drawingEnd) continue
            element.render(ctx)
            matrices.translate(0f, 9f, 0f)
        }
        ctx.popScissor()
        matrices.pop()
    }
}