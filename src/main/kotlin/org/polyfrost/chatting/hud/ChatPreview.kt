package org.polyfrost.chatting.hud

import net.minecraft.client.Minecraft
//? if > 1.8.9
import net.minecraft.client.gui.components.ComponentRenderUtils
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.polyfrost.chatting.chat.ChatDimensions
import org.polyfrost.chatting.config.ChattingConfig
//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage
import net.minecraft.client.multiplayer.chat.GuiMessageSource
//?} else {
/*//~ if =1.8.9 'net.minecraft.client.GuiMessage' -> 'net.minecraft.client.gui.ChatMessage'
import net.minecraft.client.GuiMessage
*///?}
//? if = 1.8.9 {
/*import net.minecraft.client.render.TextRenderUtils
import net.minecraft.text.LiteralText
*///?}

object ChatPreview {

    private val MESSAGES = listOf(
        "§e§lChatting",
        "§7This is a preview of your chat window.",
        "§b<Wyvest>§r Chatting for Fabric is out NOW!",
        "§b<Steve>§r Awesome!",
        "§b<Alex>§r Let's go!",
    )

    //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
    private var cached: List<GuiMessage.Line>? = null
    private var cachedWidth = -1

    /** ordered newest first like the vanilla trimmed messages */
    @JvmStatic
    //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
    fun lines(): List<GuiMessage.Line> {
        val mc = Minecraft.getInstance()
        val scale = mc.options.chatScale().get().toFloat()
        var maxWidth = Mth.floor(ChatDimensions.width() / scale)
        if (ChattingConfig.showChatHeads && ChattingConfig.offsetNonPlayerMessages) maxWidth -= 10
        var result = cached
        if (result == null || cachedWidth != maxWidth) {
            result = build(mc, maxWidth)
            cached = result
            cachedWidth = maxWidth
        }
        return result
    }

    //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
    private fun build(mc: Minecraft, maxWidth: Int): List<GuiMessage.Line> {
        //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
        val lines = ArrayList<GuiMessage.Line>()
        for (text in MESSAGES) {
            //~ if =1.8.9 'Component.literal(text)' -> 'LiteralText(text) as Component'
            val content = Component.literal(text)
            //~ if =1.8.9 'ComponentRenderUtils.wrapComponents(content, maxWidth, mc.font)' -> 'TextRenderUtils.wrapText(content, maxWidth, mc.font, false, false)'
            val wrapped = ComponentRenderUtils.wrapComponents(content, maxWidth, mc.font)
            //? if >=26 {
            val message = GuiMessage(0, content, null, GuiMessageSource.SYSTEM_CLIENT, null)
            //?}
            for (i in wrapped.indices) {
                val endOfEntry = i == wrapped.size - 1
                //? if >=26 {
                lines.add(0, GuiMessage.Line(message, wrapped[i], endOfEntry))
                //?} elif > 1.8.9 {
                /*lines.add(0, GuiMessage.Line(0, wrapped[i], null, endOfEntry))
                *///?} else
                //lines.add(0, ChatMessage(0, wrapped[i], 0))
            }
        }
        return lines
    }
}
