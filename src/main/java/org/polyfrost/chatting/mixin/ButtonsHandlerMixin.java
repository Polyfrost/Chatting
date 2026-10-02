package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.compat.TextTunnelsCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.olim.text_tunnels.ButtonsHandler", remap = false)
public class ButtonsHandlerMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private static void chatting$hideButtons(CallbackInfo ci) {
        if (TextTunnelsCompat.getSuppressing()) ci.cancel();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = false)
    private static void chatting$swallowButtonClicks(CallbackInfo ci) {
        if (TextTunnelsCompat.getSuppressing()) ci.cancel();
    }
}
