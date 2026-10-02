package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHeadState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_ChatHeadsLifecycle {
    @Inject(method = "refreshChat", at = @At("HEAD"))
    private void chatting$resetChatHeadSequence(CallbackInfo ci) {
        // refreshChat recreates the drawn lines from history.  Keeping the
        // previous live message here makes the recreated lines look like one
        // long consecutive run and can leave every cached head null.
        ChatHeadState.resetConsecutiveTracking();
    }

    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void chatting$beginChatHeadDetection(IChatComponent component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHeadState.currentComponent = component;
        ChatHeadState.lineVisible = false;
        // Resolved here rather than in ChatLine's constructor: the wrap width is
        // needed before any ChatLine for this message exists.
        ChatHeadState.pendingHead = ChatHeadState.detect(component);
    }

    // A drawn head shifts the text right by 10 px (GuiNewChatMixin_TextRendering),
    // but vanilla wraps against the full chat width, so offset lines overflowed the
    // background. Upstream fix for modern versions: polyfrost/Chatting#165.
    @ModifyArg(
        method = "setChatLine",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;splitText(Lnet/minecraft/util/IChatComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;"
        ),
        index = 1
    )
    private int chatting$headWrapWidth(int width) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return width;
        if (ChatHeadState.pendingHead == null && !ChattingConfig.INSTANCE.getOffsetNonPlayerMessages()) return width;
        return width - 10;
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
        ChatHeadState.pendingHead = null;
    }
}
*///?}
