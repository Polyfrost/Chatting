package org.polyfrost.chatting.mixin;

import org.lwjgl.nanovg.NanoVG;
import org.polyfrost.oneconfig.api.ui.v1.internal.RendererImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RendererImpl.class, remap = false)
public class RendererImplMixin {

    @ModifyArg(method = "loadImage", at = @At(value = "INVOKE", target = "Lorg/polyfrost/oneconfig/api/ui/v1/api/NanoVgApi;createImage(FFLjava/nio/ByteBuffer;I)I"), index = 3)
    private int createImage(int flags) {
        return NanoVG.NVG_IMAGE_NEAREST;
    }
}