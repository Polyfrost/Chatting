package org.polyfrost.chatting.animation

import org.polyfrost.polyui.animate.Animation

class DummyAnimation(value: Float) : Animation(0L, value, value) {

    override fun getValue(percent: Float) = 1f

    override fun clone(): Animation = DummyAnimation(to)
}