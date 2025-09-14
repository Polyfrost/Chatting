package org.polyfrost.chatting.component

import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import org.polyfrost.chatting.ChatWindow
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class ChatPendingComponent(window: ChatWindow): ChatLineComponent(window) {

    override fun getText(): OrderedText? {
        return Text.translatable("chat.queue", mc.messageHandler.unprocessedMessageCount).asOrderedText()
    }

    override fun getTextColor(): Int {
        return 16777215 + (128 shl 24)
    }

}