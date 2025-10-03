package org.polyfrost.chatting.hook

import org.polyfrost.polyui.data.PolyImage

interface ChatLineHook {

    fun `chatting$setChatHead`(head: PolyImage?)

    fun `chatting$getChatHead`(): PolyImage?

}