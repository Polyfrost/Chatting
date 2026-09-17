package org.polyfrost.chatting.chat

/**
 * The old HUD wrapper only supplied optional input-box settings. Ornithe draws
 * the input field through GuiChat, so retaining this tiny state object avoids
 * coupling the port to OneConfig's removed LegacyHud API.
 */
class ChatInputBox {
    var compactInputBox = false
    var inputFieldDraft = false
    private var backgroundVisible = true

    fun drawBG() = Unit

    fun setBackground(value: Boolean) {
        backgroundVisible = value
    }
}
