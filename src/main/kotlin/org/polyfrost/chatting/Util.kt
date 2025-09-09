@file:JvmName("Util")
package org.polyfrost.chatting

import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.OmniResolution
import org.polyfrost.chatting.component.LegacyComponent

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

var legacyComponents = ArrayList<LegacyComponent>()

fun renderLegacy(matrixStack: OmniMatrixStack) {
    val removeQueue = mutableListOf<LegacyComponent>()
    for (component in legacyComponents) {
        component.render(matrixStack)
        if (component.remove) {
            removeQueue.add(component)
        }
    }
    removeQueue.forEach {
        legacyComponents.remove(it)
    }
}