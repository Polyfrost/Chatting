package org.polyfrost.chatting.component

import dev.deftu.omnicore.api.client.events.input.InputEvent
import dev.deftu.omnicore.api.client.input.OmniKey
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.omnicore.api.client.render.OmniTextRenderer
import dev.deftu.textile.minecraft.MCSimpleTextHolder
import org.polyfrost.chatting.core.McChatLine
import org.polyfrost.chatting.animation.DummyAnimation
import org.polyfrost.chatting.core.McChat
import org.polyfrost.chatting.core.chatComponents
import org.polyfrost.chatting.core.copyToClipboard
import org.polyfrost.chatting.core.editorMessages
import org.polyfrost.chatting.core.getMessages
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.core.mcScale
import org.polyfrost.chatting.core.toChatLine
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.PolyUI
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Linear
import org.polyfrost.polyui.color.argb
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.by
import org.polyfrost.polyui.unit.ms
import kotlin.math.*

class ChatComponent(val window: ChatWindow) : LegacyHud.LegacyHudComponent(window, size = 100f by 100f) {

    var elements = ArrayList<ChatLineElement>()

    var selectedElements = ArrayList<ChatLineElement>()

    var currentHovered: ChatLineElement? = null

    var lastHovered: ChatLineElement? = null

    var hovered = false

    var lineHeight = 0f

    var scrollAmount: Animation = DummyAnimation(0f)

    var translateAmount = 0f

    var drawingStart = 0

    var drawingEnd = 0

    init {
        chatComponents.add(this)
    }

    override var visibleSize: Vec2
        get() {
            return if (hovered && !HudManager.isEditing) {
                super.visibleSize + Vec2(McChat.buttonGroup.buttons.size * 10 * mcScale * scaleX, 0f)
            } else {
                super.visibleSize
            }
        }
        set(value) {
            super.visibleSize = value
        }

    fun hoverEnter() {
        hovered = true
    }

    fun hoverExit() {
        hovered = false
        currentHovered = null
    }

    fun click(event: InputEvent.MouseButton) {
        if (event.isLeftButton) {
            leftClick()
        } else if (event.isRightButton) {
            rightClick()
        }
    }

    fun keyType(key: OmniKey) {
        if (OmniKeyboard.isCtrlKeyPressed && key == OmniKeys.KEY_A) {
            selectedElements.clear()
            selectedElements.addAll(elements)
        }
    }

    fun onClose() {
        hoverExit()
        selectedElements.clear()
        scrollAmount = DummyAnimation(0f)
    }

    fun leftClick() {
        val hovered = currentHovered ?: return
        if (OmniKeyboard.isShiftKeyPressed) {
            selectedElements.clear()
            val currentIndex = elements.indexOf(hovered)
            val lastIndex = if (lastHovered == null) 0 else elements.indexOf(lastHovered)
            for (i in min(lastIndex, currentIndex)..max(lastIndex, currentIndex)) {
                selectedElements.add(elements[i])
            }
        } else {
            if (!OmniKeyboard.isCtrlKeyPressed || !OmniKeyboard.isAltKeyPressed) {
                selectedElements.clear()
            }
            selectedElements = ArrayList(elements.filter { it in selectedElements || it == hovered })
            lastHovered = hovered
        }
    }

    fun rightClick() {
        copyMessages()
    }

    fun getSelected(): Collection<ChatLineElement> {
        return elements.filter { it in selectedElements || it == currentHovered }
    }

    fun copyMessages() {
        val stringBuilder = StringBuilder()
        val selected = getSelected()
        for (element in selected) {
            stringBuilder.append(element.string)
            if (element != selected.last()) {
                stringBuilder.append("\n")
            }
        }
        stringBuilder.toString().copyToClipboard()
        selectedElements.clear()
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

    fun getCurrentElement(mouseY: Float = OmniMouse.rawY.toFloat()) {
        if (elements.isEmpty()) return
        val index = elements.size - 1 - floor((y + (height + lineHeight * scrollAmount.value) * scaleY - mouseY) / (lineHeight * scaleY)).toInt()
        if (index < 0 || index >= elements.size) return
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
            addMessage(message.toChatLine())
        }
        window.update()
    }

    fun addAllMessages() {
        getMessages().reversed().forEach {
            addMessage(it)
        }
        window.update()
    }

    fun removeAllMessages(update: Boolean) {
        elements.clear()
        if (update) window.update()
    }

    fun addMessage(chatLine: McChatLine, update: Boolean = false) {
        var i = 320
        //#if MC > 1.16.5
        val icon = chatLine.icon
        if (icon != null) {
            i -= icon.width + 4 + 2
        }
        //#endif
        val head = (chatLine as ChatLineHook).`chatting$getChatHead`()
        val hasHead = head != null
        if (hasHead) {
            i -= 10
        }
        //#if MC >= 1.16.5
        val strings = OmniTextRenderer.wrapLines(chatLine.comp_893.string, i)
        val lines = net.minecraft.client.util.ChatMessages.breakRenderedChatMessageLines(chatLine.comp_893, i, mc.textRenderer)
        //#else
        //$$ val lines = net.minecraft.client.util.Texts.wrapLines(chatLine.text, i, mc.textRenderer, false, false)
        //#endif
        lines.withIndex().forEach {
            val element = ChatLineElement(it.value,
                //#if MC >= 1.16.5
                strings.getOrElse(it.index) {
                    MCSimpleTextHolder(chatLine.comp_893.string)
                }.asString(),
                //#endif
                chatLine,
                hasHead && it.value == lines.first(), head)
            elements.add(element)
        }

        while (elements.size > 100) {
            elements.removeFirst()
        }

        if (update) window.update()
    }

    fun removeMessage(chatLine: McChatLine, update: Boolean = false) {
        elements.removeIf { it.fullMessage == chatLine }
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
        super.preRender(delta)
    }

    override fun render() {
//        renderer.pushScissor(x, y, width * scaleX, height * scaleY)
        renderer.push()
        renderer.translate(x, y - translateAmount * lineHeight)
        for ((index, element) in elements.withIndex()) {
            if (!element.renders) continue
            if (index !in drawingStart..drawingEnd) continue
            val color = when ((element)) {
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
            renderer.rect(0f, 0f, width, lineHeight, element.color)
            if (!HudManager.isEditing && element == currentHovered) {
                McChat.buttonGroup.render(renderer, width + mcScale)
            }
            if (element.hasHead) {
                renderer.image(element.head!!, 4 * mcScale, 1 * mcScale, 8f * mcScale, 8f * mcScale, colorMask = 0xFFFFFF or (element.alpha shl 24))
            }
            //#if MC > 1.16.5
            element.fullMessage.indicator?.let { indicator ->
                val ac = indicator.comp_899 or (element.alpha shl 24)
                renderer.rect(0f, 0f, 2 * mcScale, lineHeight, argb(ac))
            }
            //#endif
            renderer.translate(0f, lineHeight)
        }
        renderer.pop()
//        renderer.popScissor()
    }

    fun drawLegacy(ctx: OmniRenderingContext) {
        if (elements.isEmpty()) return
        val matrices = ctx.matrices
        matrices.push()
        matrices.translate(x / mcScale, y / mcScale, 0f)
        matrices.scale(scaleX, scaleY, 1f)
        matrices.translate(4f, -translateAmount * 9 + 1f, 0f)
//        ctx.pushScissor(0, 0, 5, 5)
        for ((index, element) in elements.withIndex()) {
            if (!element.renders) continue
            if (index !in drawingStart..drawingEnd) continue
            element.render(ctx)
            matrices.translate(0f, 9f, 0f)
        }
//        ctx.popScissor()
        matrices.pop()
    }
}