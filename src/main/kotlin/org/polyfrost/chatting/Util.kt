@file:JvmName("Util")

package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniResolution
import net.minecraft.client.gui.DrawContext
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.ChatLineComponent
import org.polyfrost.oneconfig.api.ui.v1.UIManager
import org.polyfrost.polyui.component.Component
import org.polyfrost.polyui.utils.fastEach

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

fun renderLegacy(drawContext: DrawContext) {
    val master = UIManager.INSTANCE.defaultInstance.master
    val children: ArrayList<Component>? = master.children
    if (children == null || children.isEmpty()) return

    children.fastEach { child ->
        if (child is ChatComponent) {
            child.children!!.fastEach {
                (it as ChatLineComponent).renderLegacy(drawContext)
            }
        }
    }
}