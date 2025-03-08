package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.class)
public class GuiContainerCreativeMixin {

    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"), cancellable = true)
    private void cancelScrolling(CallbackInfo ci) {
        if (Chatting.INSTANCE.getPeeking() && ChattingConfig.INSTANCE.getPeekScrolling()) {
            ci.cancel();
        }
    }
}