package org.polyfrost.chatting.hud

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.ComponentRenderUtils
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage
import net.minecraft.client.multiplayer.chat.GuiMessageSource
//?} else {
/*import net.minecraft.client.GuiMessage
*///?}

/** Fixed placeholder messages shown in place of real chat while the chat window HUD is being edited. */
object ChatPreview {

    private val MESSAGES = listOf(
        "§e§lChatting",
        "§7This is a preview of your chat window.",
        "§b<Wyvest>§r Chatting for modern Minecraft is out NOW!",
        "§b<Steve>§r Awesome!",
        "§b<Alex>§r Let's go!",
    )

    private var cached: List<GuiMessage.Line>? = null
    private var cachedWidth = -1

    /** Placeholder lines wrapped to the current chat width, ordered newest-first like the vanilla trimmed messages. */
    @JvmStatic
    fun lines(): List<GuiMessage.Line> {
        val mc = Minecraft.getInstance()
        val scale = mc.options.chatScale().get().toFloat()
        val maxWidth = Mth.floor(ChatComponent.getWidth(mc.options.chatWidth().get()) / scale)
        var result = cached
        if (result == null || cachedWidth != maxWidth) {
            result = build(mc, maxWidth)
            cached = result
            cachedWidth = maxWidth
        }
        return result
    }

    private fun build(mc: Minecraft, maxWidth: Int): List<GuiMessage.Line> {
        val lines = ArrayList<GuiMessage.Line>()
        for (text in MESSAGES) {
            val content = Component.literal(text)
            val wrapped = ComponentRenderUtils.wrapComponents(content, maxWidth, mc.font)
            //? if >=26 {
            val message = GuiMessage(0, content, null, GuiMessageSource.SYSTEM_CLIENT, null)
            //?}
            for (i in wrapped.indices) {
                val endOfEntry = i == wrapped.size - 1
                //? if >=26 {
                lines.add(0, GuiMessage.Line(message, wrapped[i], endOfEntry))
                //?} else {
                /*lines.add(0, GuiMessage.Line(0, wrapped[i], null, endOfEntry))
                *///?}
            }
        }
        return lines
    }
}
