@file:JvmName("Util")
package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.gui.screen.ChatScreen
import org.polyfrost.oneconfig.utils.v1.dsl.mc

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val inChatScreen: Boolean
    get() = mc.currentScreen != null && mc.currentScreen is ChatScreen
