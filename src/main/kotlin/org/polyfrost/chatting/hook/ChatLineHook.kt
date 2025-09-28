package org.polyfrost.chatting.hook

import org.polyfrost.polyui.data.PolyImage

interface ChatLineHook {

    fun `chatting$setHead`(head: PolyImage?)

    fun `chatting$getHead`(): PolyImage?

}