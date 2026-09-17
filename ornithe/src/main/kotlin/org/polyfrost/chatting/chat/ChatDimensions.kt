package org.polyfrost.chatting.chat

import org.polyfrost.chatting.config.ChattingConfig

/** Modern feature boundary for chat dimensions, backed by vanilla 1.8.9 hooks. */
object ChatDimensions {
    @JvmStatic
    fun width(): Int = ChattingConfig.customWidth

    @JvmStatic
    fun height(focused: Boolean): Int =
        if (focused) ChattingConfig.focusedHeight else ChattingConfig.unfocusedHeight
}
