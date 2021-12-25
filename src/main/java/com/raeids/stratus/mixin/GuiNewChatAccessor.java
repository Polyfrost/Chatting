package com.raeids.stratus.mixin;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin (GuiNewChat.class)
public interface GuiNewChatAccessor {
    @Accessor
    List<ChatLine> getDrawnChatLines();

    @Accessor
    int getScrollPos();
}
