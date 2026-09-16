package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import org.polyfrost.chatting.config.ChattingConfig

/**
 * Legacy chat geometry used by the mixins. The original PolyUI HUD was only a
 * persistence/editor shell; vanilla owns the actual 1.8.9 chat render path.
 */
class ChatWindow {
    var isGuiIngame = false
    var normalScale = 1f
    private var lastChatScale = -1f

    var customChatHeight: Boolean
        get() = ChattingConfig.customChatHeight
        set(value) { ChattingConfig.customChatHeight = value }

    var focusedHeight: Int
        get() = ChattingConfig.focusedHeight
        set(value) { ChattingConfig.focusedHeight = value }

    var unfocusedHeight: Int
        get() = ChattingConfig.unfocusedHeight
        set(value) { ChattingConfig.unfocusedHeight = value }

    var customChatWidth: Boolean
        get() = ChattingConfig.customChatWidth
        set(value) { ChattingConfig.customChatWidth = value }

    var customWidth: Int
        get() = ChattingConfig.customWidth
        set(value) { ChattingConfig.customWidth = value }

    fun updateMCChatScale() {
        lastChatScale = Minecraft.getMinecraft().gameSettings.chatScale
    }

    fun drawBG() = Unit

    fun canShow() = true
}
