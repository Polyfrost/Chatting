package org.polyfrost.chatting

import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler
import org.polyfrost.oneconfig.utils.v1.dsl.openUI

@Command(Chatting.MODID)
object ModCommand {

    @Handler
    private fun main() {
        ModConfig.openUI()
    }

}
