package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.config.ChattingConfig;
import net.minecraftforge.fml.client.config.GuiUtils;
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
}
