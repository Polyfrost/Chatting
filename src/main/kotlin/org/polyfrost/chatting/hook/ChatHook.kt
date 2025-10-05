package org.polyfrost.chatting.hook

import org.polyfrost.chatting.core.McChatLine

interface ChatHook {

    fun `chatting$deleteChatLine`(chatLines: Collection<McChatLine>)
}