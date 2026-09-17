package org.polyfrost.chatting

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.config.ChattingConfig
import java.nio.file.Paths

/** Fabric (Ornithe) client bootstrap for the v2 feature set. */
object Chatting : ClientModInitializer {
    const val ID = "chatting"
    const val NAME = "Chatting"
    const val VER = "2.0.6"

    val oldModDir = Paths.get("W-OVERFLOW", NAME)

    var peeking = false
        get() = ChattingConfig.chatPeek && field

    override fun initClient() {
        // Config registration is deferred by OneConfig; explicit preload makes the
        // option tree available before tab/shortcut migration reads its folder.
        ChattingConfig.preload()
        ChatTabs.initialize()
        ChatShortcuts.initialize()
    }

    fun getChatHeight(opened: Boolean): Int =
        if (opened) ChattingConfig.focusedHeight else ChattingConfig.unfocusedHeight

    fun getChatWidth(): Int = ChattingConfig.customWidth
}
