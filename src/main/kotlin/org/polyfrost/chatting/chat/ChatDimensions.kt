package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import org.polyfrost.chatting.config.ChattingConfig

object ChatDimensions {

    @JvmStatic
    fun width(): Int {
        if (ChattingConfig.customChatWidth) return ChattingConfig.chatWidth
        return ChatComponent.getWidth(Minecraft.getInstance().options.chatWidth().get())
    }

    @JvmStatic
    fun height(focused: Boolean): Int {
        if (ChattingConfig.customChatHeight) {
            return if (focused) ChattingConfig.focusedChatHeight else ChattingConfig.unfocusedChatHeight
        }
        val options = Minecraft.getInstance().options
        val option = if (focused) options.chatHeightFocused() else options.chatHeightUnfocused()
        return ChatComponent.getHeight(option.get())
    }

    fun refresh() {
        //? if >=26.2 {
        Minecraft.getInstance().gui?.hud?.chat?.rescaleChat()
        //?} else {
        /*Minecraft.getInstance().gui?.chat?.rescaleChat()
        *///?}
    }
}
