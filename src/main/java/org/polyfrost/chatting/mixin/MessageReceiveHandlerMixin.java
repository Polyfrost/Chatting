package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.compat.TextTunnelsCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "org.olim.text_tunnels.MessageReceiveHandler", remap = false)
public class MessageReceiveHandlerMixin {

    @Inject(method = "isFilterInActive", at = @At("HEAD"), cancellable = true, remap = false)
    private static void chatting$suppressForChatTabs(CallbackInfoReturnable<Boolean> cir) {
        if (TextTunnelsCompat.getSuppressing()) cir.setReturnValue(true);
    }
}
