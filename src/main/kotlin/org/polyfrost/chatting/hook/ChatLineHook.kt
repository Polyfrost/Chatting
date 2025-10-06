package org.polyfrost.chatting.hook

import org.polyfrost.chatting.component.PlayerHead

interface ChatLineHook {

    fun `chatting$setChatHead`(head: PlayerHead?)

    fun `chatting$getChatHead`(): PlayerHead?

}