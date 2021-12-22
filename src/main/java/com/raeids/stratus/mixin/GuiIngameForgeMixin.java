package com.raeids.stratus.mixin;

import com.raeids.stratus.hook.GuiIngameForgeHook;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = GuiIngameForge.class, remap = false, priority = Integer.MIN_VALUE)
public class GuiIngameForgeMixin implements GuiIngameForgeHook {
    private int stratus$x = 0;
    private int stratus$y = 0;
    @ModifyArgs(method = "renderChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", remap = true))
    private void captureTranslate(Args args) {
        stratus$x = Math.round(args.get(0));
        stratus$y = Math.round(args.get(1));
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
