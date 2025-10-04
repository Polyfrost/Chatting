package org.polyfrost.chatting

import org.polyfrost.chatting.core.ChattingClient
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
//$$ @Mod(ChattingConstants.MODID)
//#else
//$$ @Mod(modid = ChattingConstants.MODID, version = ChattingConstants.VERSION)
//#endif
//#endif
class ChattingEntrypoint
    //#if FABRIC
    : ClientModInitializer
    //#endif
{

    //#if FORGE && MC >= 1.16.5
    //$$ init {
    //$$     FMLJavaModLoadingContext.get().modEventBus.addListener(this::onInitializeClient)
    //$$ }
    //#endif

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

        ChattingClient.initialize()
    }

}
