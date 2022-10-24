package cc.woverflow.chatting.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.config.ChattingConfig

@Command(value = Chatting.ID, description = "Access the " + Chatting.NAME + " GUI.")
class ChattingCommand {
    @Main
    fun main() {
        ChattingConfig.openGui()
    }
}