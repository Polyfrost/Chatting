package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_ChatHeight {
    @Shadow public abstract boolean getChatOpen();

    @Inject(method = "getChatHeight", at = @At("HEAD"), cancellable = true)
    private void customHeight_getChatHeight(CallbackInfoReturnable<Integer> cir) {
        if (ChattingConfig.INSTANCE.getChatWindow().getCustomChatHeight())
            cir.setReturnValue(Chatting.INSTANCE.getChatHeight(getChatOpen()));
    }

    @Inject(method = "getChatWidth", at = @At("HEAD"), cancellable = true)
    private void customWidth_getChatWidth(CallbackInfoReturnable<Integer> cir) {
        if (ChattingConfig.INSTANCE.getChatWindow().getCustomChatWidth())
            cir.setReturnValue(Chatting.INSTANCE.getChatWidth());
    }
}
