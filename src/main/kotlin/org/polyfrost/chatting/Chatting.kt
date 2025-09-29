package org.polyfrost.chatting

import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.input.InputEvent
import dev.deftu.omnicore.api.client.events.input.InputState
import dev.deftu.omnicore.api.client.screen.isInChatScreen
import dev.deftu.omnicore.api.client.screen.isInScreen
import dev.deftu.omnicore.api.eventBus
import org.polyfrost.chatting.event.MouseActionEvent
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.UIManager

//#if FORGE
//$$ @net.minecraftforge.fml.common.Mod(
//#if MC >=1.20.1 || MC <=1.12.2
//$$     modid = Chatting.MODID,
//$$     name = Chatting.NAME,
//$$     version = Chatting.VERSION,
//$$     modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter"
//#else
//$$     value = Chatting.MODID
//#endif
//$$ )
//#endif
object Chatting
//#if FABRIC
    : net.fabricmc.api.ClientModInitializer
//#endif
{

    const val MODID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

    fun initialize() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
        HudManager.register(ChatWindow(preview = true))
        eventBus.register(this)

        eventHandler { event: MouseInputEvent.Moved ->
            if (!isInScreen || !isInChatScreen) return@eventHandler
            UIManager.INSTANCE.defaultInstance.inputManager.mouseMoved(event.x, event.y)
        }
    }

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseButton) {
        if (event.state == InputState.RELEASED) return
        if (!isInScreen || !isInChatScreen) return
        UIManager.INSTANCE.defaultInstance.inputManager.mouseOver.let {
            EventManager.INSTANCE.post(MouseActionEvent.Companion.Click(it, event.button.code))
        }
    }

    //#if FORGE
    //$$ @net.minecraftforge.fml.common.Mod.EventHandler
    //$$ fun onFMLInit(event: net.minecraftforge.fml.common.event.FMLInitializationEvent) {
    //$$     initialize()
    //$$ }
    //#else
    override fun onInitializeClient() {
        initialize()
    }
    //#endif

}
