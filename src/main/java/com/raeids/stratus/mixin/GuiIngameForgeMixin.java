package com.raeids.stratus.mixin;

import com.raeids.stratus.hook.GuiIngameForgeHook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiIngameForge.class, remap = false)
public class GuiIngameForgeMixin implements GuiIngameForgeHook {
    private int stratus$x = 0;
    private int stratus$y = 0;
    @Redirect(method = "renderChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", remap = true))
    private void captureTranslate(float x, float y, float z) {
        stratus$x = Math.round(x);
        stratus$y = Math.round(y);
        GlStateManager.translate(x, y, z);
    }

    @Override
    public int getX() {
        return stratus$x;
    }

    @Override
    public int getY() {
        return stratus$y;
    }
}
