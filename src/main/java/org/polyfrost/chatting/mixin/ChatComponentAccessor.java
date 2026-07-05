package org.polyfrost.chatting.mixin;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {

    @Accessor("chatScrollbarPos")
    int chatting$getScrollbarPos();

    @Accessor("trimmedMessages")
    List<GuiMessage.Line> chatting$getTrimmedMessages();

    @Accessor("allMessages")
    List<GuiMessage> chatting$getAllMessages();

    @Invoker("getLineHeight")
    int chatting$getLineHeight();

    @Invoker("getScale")
    double chatting$getScale();

    @Invoker("getWidth")
    int chatting$getWidth();

    @Invoker("refreshTrimmedMessages")
    void chatting$refreshTrimmedMessages();

    //? if <1.21.11 {
    /*@Invoker("getMessageEndIndexAt")
    int chatting$getMessageEndIndexAt(double x, double y);

    @Invoker("screenToChatY")
    double chatting$screenToChatY(double y);
    *///?}
}
