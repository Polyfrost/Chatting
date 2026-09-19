package org.polyfrost.chatting

import org.polyfrost.chatting.config.ChattingConfig
import java.nio.file.Paths

/** Global feature state shared by the 1.8.9 renderer adapters. */
object Chatting {
    const val ID = ChattingConstants.ID
    const val NAME = ChattingConstants.NAME
    const val VER = ChattingConstants.VERSION

    val oldModDir = Paths.get("W-OVERFLOW", NAME)

    var peeking = false
        get() = ChattingConfig.chatPeek && field

}
