package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import org.polyfrost.chatting.config.ChattingConfig

/** Modern feature boundary for chat dimensions, backed by vanilla 1.8.9 hooks. */
object ChatDimensions {
    @JvmStatic
    fun width(): Int = ChattingConfig.customWidth.coerceIn(40, 2160)

    @JvmStatic
    fun height(focused: Boolean): Int =
        (if (focused) ChattingConfig.focusedHeight else ChattingConfig.unfocusedHeight).coerceIn(20, 2160)

    @JvmStatic
    fun refresh() {
        Minecraft.getMinecraft().ingameGUI.chatGUI.refreshChat()
    }
}
