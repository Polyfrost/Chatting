package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.Gui;
import org.polyfrost.chatting.hook.ChatHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "drawRect", at = @At("HEAD"), cancellable = true)
    private static void cancelRect(int left, int top, int right, int bottom, int color, CallbackInfo ci) {
        if (ChatHook.cancelRect) {
            ChatHook.cancelRect = false;
            ci.cancel();
        }
    }

}