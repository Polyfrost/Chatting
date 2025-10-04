package org.polyfrost.chatting.core

import org.polyfrost.chatting.ChattingConstants
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler
import org.polyfrost.oneconfig.utils.v1.dsl.openUI

@Command(ChattingConstants.MODID)
object ModCommand {

    @Handler
    private fun main() {
        ModConfig.openUI()
    }

}
