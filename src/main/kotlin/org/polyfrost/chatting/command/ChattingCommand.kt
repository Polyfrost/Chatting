package org.polyfrost.chatting.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig

@Command(value = Chatting.ID, description = "Access the " + Chatting.NAME + " GUI.")
class ChattingCommand {
    @Main
    fun main() {
        ChattingConfig.openGui()
    }
}