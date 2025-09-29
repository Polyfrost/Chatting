package org.polyfrost.chatting.util

import dev.deftu.textile.minecraft.MCTextHolder

class MessageInfo(
    val text: MCTextHolder<*>,
    val indicator: Object?
) {

    val string = text.asString()

    var creationTick = getCurrentTick()
}