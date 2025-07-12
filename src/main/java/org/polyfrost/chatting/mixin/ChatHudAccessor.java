package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {

    @Accessor
    List<ChatHudLine.Visible> getVisibleMessages();

    @Accessor
    int getScrolledLines();



//    @Accessor
//    boolean getHasUnreadNewMessages();
//
//    @Invoker("getHeight")
//    int getHeight();
//
//    @Invoker("getWidth")
//    int getWidth();
//
//    @Invoker("getMessageIndex")
//    int getMessageIndex(double x, double y);
//
//    @Invoker("toChatLineX")
//    double toChatLineX(double x);
//
//    @Invoker("toChatLineY")
//    double toChatLineY(double x);
//
    @Invoker
    int invokeGetIndicatorX(ChatHudLine.Visible visible);
//
    @Invoker
    void invokeDrawIndicatorIcon(DrawContext drawContext, int i, int j, MessageIndicator.Icon icon);

    @Invoker
    double invokeGetMessageOpacityMultiplier(int i);

    @Invoker
    int invokeGetLineHeight();
//
//    @Invoker("isChatHidden")
//    boolean isChatHidden();
//
//    @Invoker("getChatScale")
//    double getChatScale();

}
