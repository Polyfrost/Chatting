package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiNewChat.class, priority = 1100)
public class GuiNewChatMixin_ChatPeek {

    @Inject(method = "getChatOpen", at = @At("HEAD"), cancellable = true)
    private void chatPeek(CallbackInfoReturnable<Boolean> cir) {
        if (Chatting.INSTANCE.getPeeking()) cir.setReturnValue(true);
    }

}