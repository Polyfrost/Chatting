package org.polyfrost.chatting.integration

//? if > 1.8.9 {
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
//?} else {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen

/** Opens Chatting's OneConfig page from Mod Menu's legacy 1.8.9 API. */
class ChattingModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
        ConfigScreenFactory { OneConfigUIScreen(ChattingConfig.id) }
}
*///?}
