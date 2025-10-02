package org.polyfrost.chatting.util

import dev.deftu.textile.minecraft.MCTextHolder
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.data.PolyImage

class MessageInfo(
    val id: Int,
    val message: MCTextHolder<*>,
    var creationTick: Int,
    val indicator: Any? = null,
) {

    var headImage: PolyImage? = null

    val string = message.asString()

}