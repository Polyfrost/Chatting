package org.polyfrost.chatting.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen

class ChattingModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        // OneConfig's own compat layer also collects this factory and would register a duplicate entry
        if (!FabricLoader.getInstance().isModLoaded("modmenu")) return null
        return ConfigScreenFactory { OneConfigUIScreen(ChattingConfig.id) }
    }
}
