package org.polyfrost.chatting.mixin;

import net.minecraft.entity.player.InventoryPlayer;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {
    @Inject(method = "changeCurrentItem", at = @At("HEAD"), cancellable = true)
    private void cancelHotbarScrolling(int direction, CallbackInfo ci) {
        if (Chatting.INSTANCE.getPeaking() && ChattingConfig.INSTANCE.getPeakScrolling()) {
            ci.cancel();
        }
    }
}