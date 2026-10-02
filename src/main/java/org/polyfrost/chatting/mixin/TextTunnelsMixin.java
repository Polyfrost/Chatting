package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.compat.TextTunnelsCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.olim.text_tunnels.Text_tunnels", remap = false)
public class TextTunnelsMixin {

    @Inject(method = "configUpdated", at = @At("TAIL"), remap = false)
    private static void chatting$onConfigUpdated(CallbackInfo ci) {
        TextTunnelsCompat.reevaluate();
    }
}
