package org.polyfrost.chatting.command

import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import kotlin.concurrent.thread

@Command(value = Chatting.ID, description = "Access the " + Chatting.NAME + " GUI.")
class ChattingCommand {
    @Main
    fun main() {
        ChattingConfig.openGui()
    }

    @SubCommand
    fun loop(amt: Int) {
        thread {
            for (i in 1..amt) {
                UChat.chat(i)
            }
        }
    }

    @SubCommand
    fun loopDelay(amt: Int, delay: Int) {
        thread {
            for (i in 1..amt) {
                UChat.chat(i)
                Thread.sleep(delay.toLong())
            }
        }
    }
}