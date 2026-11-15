package org.polyfrost.chatting

import org.polyfrost.chatting.config.ChattingConfig

object Chatting {

    var peeking = false
        get() = ChattingConfig.chatPeek && field

    @JvmField
    var noShadowPass = false
}
