package com.raeids.stratus.command

import com.raeids.stratus.Stratus
import com.raeids.stratus.config.StratusConfig
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

object StratusCommand : Command(Stratus.ID, true) {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(StratusConfig.gui())
    }
}