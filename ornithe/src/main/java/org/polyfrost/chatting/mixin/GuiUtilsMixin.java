package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiUtils.class, remap = false)
public class GuiUtilsMixin {
    @Shadow
    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
    }

    @Redirect(method = "drawHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/config/GuiUtils;drawGradientRect(IIIIIII)V"))
    private static void redirectBackground(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        if (!ChattingConfig.INSTANCE.getRemoveTooltipBackground()) {
            drawGradientRect(zLevel, left, top, right, bottom, startColor, endColor);
        }
    }

    @Redirect(method = "drawHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private static int redirectText(FontRenderer instance, String text, float x, float y, int color) {
        switch (ChattingConfig.INSTANCE.getTooltipTextRenderType()) {
            case 0:
                return instance.drawString(text, x, y, color, false);
//            case 2: TODO
//                return TextRenderer.drawBorderedText(text, x, y, color, 255);
            default:
                return instance.drawStringWithShadow(text, x, y, color);
        }
    }
}
