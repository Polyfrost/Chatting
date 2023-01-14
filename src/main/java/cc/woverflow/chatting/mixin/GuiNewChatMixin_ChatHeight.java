package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.Chatting;
import cc.woverflow.chatting.config.ChattingConfig;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_ChatHeight {
    @Shadow public abstract boolean getChatOpen();

    @Inject(method = "getChatHeight", at = @At("HEAD"), cancellable = true)
    private void customHeight_getChatHeight(CallbackInfoReturnable<Integer> cir) {
        if (ChattingConfig.INSTANCE.getCustomChatHeight())
            cir.setReturnValue(Chatting.INSTANCE.getChatHeight(getChatOpen()));
    }
}
