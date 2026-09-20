package org.polyfrost.chatting.chat

//? if > 1.8.9 {
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
//?} else {
/*import net.minecraft.client.Minecraft
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
*///?}
