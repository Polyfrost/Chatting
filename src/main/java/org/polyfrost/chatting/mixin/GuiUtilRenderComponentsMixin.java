package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiUtilRenderComponents;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.utils.ChatHeadHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiUtilRenderComponents.class)
public class GuiUtilRenderComponentsMixin {
    @Redirect(method = "splitText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"))
    private static int modifyChatLineX(net.minecraft.client.gui.FontRenderer fontRenderer, String text) {
        if (ChattingConfig.INSTANCE.getShowChatHeads() && (ChattingConfig.INSTANCE.getOffsetNonPlayerMessages() || ChatHeadHooks.INSTANCE.detect(text, null))) {
            return fontRenderer.getStringWidth(text) + 10;
        }
        // TODO: time thingy?
        return fontRenderer.getStringWidth(text);
    }
}
