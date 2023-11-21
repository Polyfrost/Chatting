package org.polyfrost.chatting.utils

import cc.polyfrost.oneconfig.gui.animations.Animation

class EaseOutQuart(duration: Float, start: Float, end: Float, reverse: Boolean) : Animation(duration, start, end, reverse) {
    override fun animate(x: Float) = -1 * (x - 1) * (x - 1) * (x - 1) * (x - 1) + 1
}