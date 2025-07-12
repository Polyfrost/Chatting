package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.polyfrost.chatting.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

//    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//    private void cancelChat(DrawContext drawContext, int i, int j, int k, boolean bl, CallbackInfo ci) {
//        if (ModConfig.INSTANCE.enabled) ci.cancel();
//    }
}
