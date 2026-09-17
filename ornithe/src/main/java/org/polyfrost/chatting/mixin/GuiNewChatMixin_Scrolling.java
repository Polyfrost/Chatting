package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Retains the scrollbar toggle while vanilla owns scroll interpolation. */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Scrolling extends Gui {
    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 1))
    private void chatting$drawScrollBar(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 2))
    private void chatting$drawScrollBarBackground(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) drawRect(left, top, right, bottom, color);
    }
}
