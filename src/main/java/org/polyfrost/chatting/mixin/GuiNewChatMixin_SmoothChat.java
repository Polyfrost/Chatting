package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.chat.SmoothChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/^*
 * 1.8.9 render adapter for Chatting's modern smooth-message state machine.
 ^/
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_SmoothChat {
    @Shadow private boolean isScrolled;
    @Shadow public abstract float getChatScale();

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void chatting$startMessageAnimation(IChatComponent component, int chatLineId, CallbackInfo ci) {
        SmoothChat.INSTANCE.start();
    }

    @Inject(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void chatting$translateNewMessage(int updateCounter, CallbackInfo ci) {
        GlStateManager.translate(0f, SmoothChat.INSTANCE.translateY(isScrolled, getChatScale()), 0f);
    }

    @ModifyArg(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"
        ),
        index = 3
    )
    private int chatting$fadeNewMessage(int color) {
        return SmoothChat.INSTANCE.fadeColor(color);
    }
}
*///?}
