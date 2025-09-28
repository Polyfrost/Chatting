package org.polyfrost.chatting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

//#if FORGE
//$$ @Mixin(net.minecraft.client.gui.GuiNewChat.class)
//#else
@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
//#endif
public interface ChatAccessor {


    //#if FORGE
    //$$ @Accessor("chatLines")
    //$$ List<net.minecraft.client.gui.ChatLine> getMessages();
    //#else
    @Accessor
    List<net.minecraft.client.gui.hud.ChatHudLine> getMessages();
    //#endif

}
