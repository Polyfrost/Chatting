package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Adapts the vanilla 200-tick fade clock to Chatting's configured duration. */
@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_Fade {
    @ModifyVariable(method = "drawChat", at = @At(value = "STORE", ordinal = 0), ordinal = 6)
    private int chatting$fadeDelay(int age) {
        return age + 200 - (int) (ChattingConfig.INSTANCE.getFadeTime() * 20f);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatOpen()Z"))
    private boolean chatting$keepMessagesVisible(GuiNewChat chat) {
        return !ChattingConfig.INSTANCE.getFade() || chat.getChatOpen();
    }
}
