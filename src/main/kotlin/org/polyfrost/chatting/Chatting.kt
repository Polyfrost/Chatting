package org.polyfrost.chatting

import org.polyfrost.chatting.config.ChattingConfig

object Chatting {

    const val ID = ChattingConstants.ID
    const val NAME = ChattingConstants.NAME

    var peeking = false
        get() = ChattingConfig.chatPeek && field

    @JvmField
    var noShadowPass = false
}
