package org.polyfrost.chatting.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen

class ChattingModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
        ConfigScreenFactory { OneConfigUIScreen(ChattingConfig.id) }
}
