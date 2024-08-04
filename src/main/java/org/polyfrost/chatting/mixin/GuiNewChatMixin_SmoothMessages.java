package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.utils.EaseOutQuart;
import org.polyfrost.chatting.utils.ModCompatHooks;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

/**
 * Taken from BetterChat under LGPL 3.0
 * <a href="https://github.com/LlamaLad7/Better-Chat/blob/1.8.9/LICENSE">https://github.com/LlamaLad7/Better-Chat/blob/1.8.9/LICENSE</a>
 */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_SmoothMessages {
    @Shadow
    private boolean isScrolled;

    @Shadow
    public abstract float getChatScale();
    @Unique
    private int chatting$newLines;

    @Unique
    private EaseOutQuart chatting$easeOutQuart;
    @Unique
    private float chatting$animationPercent;
    @Unique
    private int chatting$lineBeingDrawn;

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void modifyChatRendering(CallbackInfo ci) {
        if (chatting$easeOutQuart != null) {
            if (chatting$easeOutQuart.isFinished()) {
                chatting$easeOutQuart = null;
            } else {
                chatting$animationPercent = chatting$easeOutQuart.get();
            }
        } else {
            chatting$animationPercent = 1;
        }
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void translate(CallbackInfo ci) {
        float y = 0;
        if (ChattingConfig.INSTANCE.getSmoothChat() && !this.isScrolled) {
            y += (9 - 9 * chatting$animationPercent) * this.getChatScale();
        }
        GlStateManager.translate(0, y, 0);
    }

    @ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false), index = 0)
    private int getLineBeingDrawn(int line) {
        chatting$lineBeingDrawn = line;
        return line;
    }

    @ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int modifyTextOpacity(int original) {
        if (ChattingConfig.INSTANCE.getSmoothChat() && chatting$lineBeingDrawn <= chatting$newLines) {
            int opacity = (original >> 24) & 0xFF;
            opacity *= chatting$animationPercent;
            return (original & ~(0xFF << 24)) | (opacity << 24);
        } else {
            return original;
        }
    }

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void resetPercentage(IChatComponent chatComponent, int chatLineId, CallbackInfo ci) {
        if (!EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText()).toLowerCase(Locale.ENGLISH).contains(ChatSearchingManager.INSTANCE.getLastSearch().toLowerCase(Locale.ENGLISH))) {
            return;
        }
        if (ModCompatHooks.getBetterChatSmoothMessages()) {
            return;
        }
        if (ChatTabs.INSTANCE.getHasCancelledAnimation()) {
            ChatTabs.INSTANCE.setHasCancelledAnimation(false);
            return;
        }
        chatting$easeOutQuart = new EaseOutQuart((1.0f - ChattingConfig.INSTANCE.getMessageSpeed()) * 1000f, 0f, 1f, false);
    }

    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<IChatComponent> setNewLines(List<IChatComponent> original) {
        chatting$newLines = original.size() - 1;
        return original;
    }
}
