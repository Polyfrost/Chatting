package org.polyfrost.chatting.mixin;

import net.minecraftforge.client.GuiIngameForge;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin {

    @ModifyArgs(method = "renderChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"))
    private void cancelTranslate(Args args) {
        args.set(1, 0f);
    }

    @Inject(method = "renderChat", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void cancelChat(int width, int height, CallbackInfo ci) {
        if (!ChattingConfig.INSTANCE.getChatWindow().canShow()) ci.cancel();
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderChat(II)V"))
    private void setBypass(float partialTicks, CallbackInfo ci) {
        ChattingConfig.INSTANCE.getChatWindow().setGuiIngame(true);
    }

}
