package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import org.polyfrost.chatting.chat.ChatScreenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// the constructor starts AWT before client entrypoints run
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljavax/imageio/ImageIO;setUseCache(Z)V"))
    private void chatting$allowAwtClipboard(CallbackInfo ci) {
        ChatScreenshot.allowAwtClipboard();
    }
}
*///?}
