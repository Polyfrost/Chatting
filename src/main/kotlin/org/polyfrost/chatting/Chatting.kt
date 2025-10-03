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
//#if FABRIC
import net.fabricmc.api.ClientModInitializer
//#elseif FORGE
//#if MC >= 1.16.5
//$$ import net.minecraftforge.eventbus.api.IEventBus
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
//#else
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.common.event.FMLInitializationEvent
//#endif
//#endif

//#if FORGE-LIKE
//#if MC >= 1.16.5
//$$ @Mod(Chatting.MODID)
//#else
//$$ @Mod(modid = Chatting.MODID, version = Chatting.VERSION)
//#endif
//#endif
object Chatting
    //#if FABRIC
    : ClientModInitializer
    //#endif
{

    const val MODID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

    //#if FORGE && MC >= 1.16.5
    //$$ init {
    //$$     FMLJavaModLoadingContext.get().modEventBus.addListener(this::onInitializeClient)
    //$$ }
    //#endif

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
        println(event.button)
        if (!isInScreen || !isInChatScreen) return
        UIManager.INSTANCE.defaultInstance.inputManager.mouseOver.let {
            EventManager.INSTANCE.post(MouseActionEvent.Companion.Click(it, event.button.code))
        }
    }

    //#if FABRIC
    override
    //#elseif FORGE && MC <= 1.12.2
    //$$ @Mod.EventHandler
    //#endif
    fun onInitializeClient(
        //#if FORGE
        //#if MC >= 1.16.5
        //$$ event: FMLClientSetupEvent
        //#else
        //$$ event: FMLInitializationEvent
        //#endif
        //#endif
    ) {
        //#if FORGE && MC <= 1.12.2
        //$$ if (!event.side.isClient) {
        //$$     return
        //$$ }
        //#endif

        initialize()
    }

}
