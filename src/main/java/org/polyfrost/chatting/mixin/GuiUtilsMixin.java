package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.renderer.TextRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiUtils.class, remap = false)
public class GuiUtilsMixin {

    @Inject(method = "drawHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/config/GuiUtils;drawGradientRect(IIIIIII)V"))
    private static void redirectBackground(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getRemoveTooltipBackground()) ChatHook.cancelRect = true;
    }

    @Redirect(method = "drawHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private static int redirectText(FontRenderer instance, String text, float x, float y, int color) {
        switch (ChattingConfig.INSTANCE.getTooltipTextRenderType()) {
            case 0:
                return instance.drawString(text, x, y, color, false);
            case 2:
                return TextRenderer.drawBorderedText(text, x, y, color, 255);
            default:
                return instance.drawStringWithShadow(text, x, y, color);
        }
    }

    @Inject(method = "drawGradientRect", at = @At("HEAD"), cancellable = true)
    private static void cancelRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor, CallbackInfo ci) {
        if (ChatHook.cancelRect) {
            ChatHook.cancelRect = false;
            ci.cancel();
        }
    }
}
