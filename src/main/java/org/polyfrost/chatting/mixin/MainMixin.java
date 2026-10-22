package org.polyfrost.chatting.mixin;

//? if > 1.8.9 {
import net.minecraft.client.main.Main;
import org.polyfrost.chatting.chat.ChatScreenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {
    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void chatting$allowAwtClipboard(String[] args, CallbackInfo ci) {
        ChatScreenshot.allowAwtClipboard();
    }
}
//?}
