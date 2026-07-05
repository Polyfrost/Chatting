package org.polyfrost.chatting.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen

class ChattingModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        // When Mod Menu is absent, OneConfig's own compat layer still collects this factory and would
        // register a duplicate entry next to the native config, so only expose it to Mod Menu itself.
        if (!FabricLoader.getInstance().isModLoaded("modmenu")) return null
        return ConfigScreenFactory { OneConfigUIScreen(ChattingConfig.id) }
    }
}
