package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Retains the scrollbar toggle while vanilla owns scroll interpolation. */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Scrolling extends Gui {
    @Shadow private int scrollPos;

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$stepSmoothScroll(int updateCounter, CallbackInfo ci) {
        ChatScrolling.INSTANCE.step(scrollPos);
    }

    @Redirect(method = "drawChat", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiNewChat;scrollPos:I"))
    private int chatting$renderSmoothScrollPosition(GuiNewChat instance) {
        return ChattingConfig.INSTANCE.getSmoothScrolling() ? ChatScrolling.INSTANCE.position() : scrollPos;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 1))
    private void chatting$drawScrollBar(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 2))
    private void chatting$drawScrollBarBackground(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) drawRect(left, top, right, bottom, color);
    }
}
