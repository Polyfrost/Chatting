package cc.woverflow.chattils.command

import cc.woverflow.chattils.Chattils
import cc.woverflow.chattils.config.ChattilsConfig
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

object ChattilsCommand : Command(Chattils.ID, true) {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(ChattilsConfig.gui())
    }
}