package org.polyfrost.chatting.util

import org.polyfrost.chatting.mixin.ChatAccessor
import org.polyfrost.oneconfig.utils.v1.dsl.mc

fun getChatMessages(): List<*> {
    //#if FORGE
    //$$ return (mc.ingameGUI.chatGUI as ChatAccessor).messages
    //#else
    return (mc.inGameHud.chatHud as ChatAccessor).messages
    //#endif
}

fun getCurrentTick(): Int {
    //#if FORGE
    //$$ return mc.ingameGUI.updateCounter
    //#else
    return mc.inGameHud.ticks
    //#endif
}