package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.hook.ChatHeadState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_ChatHeadsLifecycle {
    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void chatting$beginChatHeadDetection(IChatComponent component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHeadState.currentComponent = component;
        ChatHeadState.lineVisible = false;
    }

    @Inject(method = "setChatLine", at = @At(value = "NEW", target = "net/minecraft/client/gui/ChatLine", ordinal = 0))
    private void chatting$markFirstChatHeadLine(IChatComponent component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHeadState.lineVisible = true;
    }

    @Inject(
        method = "setChatLine",
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER)
    )
    private void chatting$finishFirstChatHeadLine(IChatComponent component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHeadState.lineVisible = false;
    }

    @Inject(method = "setChatLine", at = @At("RETURN"))
    private void chatting$endChatHeadDetection(IChatComponent component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHeadState.currentComponent = null;
        ChatHeadState.lineVisible = false;
    }
}
