@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.chatting.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.page.OneConfigPages

class ChattingModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        // OneConfig's own compat layer also collects this factory and would register a duplicate entry
        if (!FabricLoader.getInstance().isModLoaded("modmenu")) return null
        return ConfigScreenFactory { OneConfigUI.createScreen(OneConfigPages.modConfig(ChattingConfig.id)) as Screen? }
    }
}
