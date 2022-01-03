package cc.woverflow.chatting.command

import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.config.ChattingConfig
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

object ChattingCommand : Command(Chatting.ID, true) {

    override val commandAliases: Set<Alias> = setOf(Alias("stratus"))

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(ChattingConfig.gui())
    }
}