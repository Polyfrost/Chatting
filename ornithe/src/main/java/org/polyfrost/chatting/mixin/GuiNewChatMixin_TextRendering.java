package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Applies Chatting's text-shadow setting through the vanilla chat renderer. */
@Mixin(value = GuiNewChat.class, priority = 990)
public class GuiNewChatMixin_TextRendering {
    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int chatting$drawChatText(FontRenderer renderer, String text, float x, float y, int color) {
        return renderer.drawString(text, x, y, color, ChattingConfig.INSTANCE.getTextRenderType() != 0);
    }
}
