package org.polyfrost.chatting.hook

import com.mojang.authlib.GameProfile
import org.polyfrost.polyui.data.PolyImage

interface ChatHudLineHook {

    fun `chatting$setHead`(head: PolyImage?)

    fun `chatting$getHead`(): PolyImage?

}