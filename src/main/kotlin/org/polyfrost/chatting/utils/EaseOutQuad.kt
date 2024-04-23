package org.polyfrost.chatting.utils

import cc.polyfrost.oneconfig.gui.animations.Animation
import net.minecraft.client.Minecraft

class EaseOutQuad(duration: Float, start: Float, end: Float, reverse: Boolean): Animation(duration, start, end, reverse) {
    var startTime = 0L

    init {
        startTime = Minecraft.getSystemTime()
    }

    override fun get(): Float {
        timePassed = (Minecraft.getSystemTime() - startTime).toFloat()
        if (timePassed >= duration) return start + change
        return animate(timePassed / duration) * change + start
    }

    override fun isFinished(): Boolean {
        return (Minecraft.getSystemTime() - startTime).toFloat() >= duration
    }

    override fun animate(x: Float): Float = 1 - (1 - x) * (1 - x)
}