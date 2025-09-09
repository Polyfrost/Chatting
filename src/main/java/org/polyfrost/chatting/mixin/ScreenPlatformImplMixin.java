package org.polyfrost.chatting.mixin;

import dev.deftu.omnicore.client.render.OmniMatrixStack;
import org.polyfrost.chatting.Util;
import org.polyfrost.oneconfig.api.platform.v1.internal.ScreenPlatformImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenPlatformImpl.class, remap = false)
public class ScreenPlatformImplMixin {

    @Shadow
    private OmniMatrixStack smuggled;

    @Inject(method = "renderLegacyHuds", at = @At("HEAD"))
    private void renderLegacy(CallbackInfo ci) {
        Util.renderLegacy(smuggled);
    }
}