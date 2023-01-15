package cc.woverflow.chatting.mixin;

import cc.polyfrost.oneconfig.utils.MathUtils;
import cc.woverflow.chatting.chat.ChatSearchingManager;
import cc.woverflow.chatting.chat.ChatTabs;
import cc.woverflow.chatting.config.ChattingConfig;
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

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_SmoothMessages {
    @Shadow
    private boolean isScrolled;

    @Shadow
    public abstract float getChatScale();

    private float chatting$percentComplete; //be nice and allow other mods to access this :)
    @Unique
    private int chatting$newLines;
    @Unique
    private long chatting$prevMillis = System.currentTimeMillis();
    @Unique
    private float chatting$animationPercent;
    @Unique
    private int chatting$lineBeingDrawn;

    private void updatePercentage(long diff) {
        if (chatting$percentComplete < 1) chatting$percentComplete += 0.004f * diff;
        chatting$percentComplete = MathUtils.clamp(chatting$percentComplete, 0, 1);
    }

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void modifyChatRendering(CallbackInfo ci) {
        long current = System.currentTimeMillis();
        long diff = current - chatting$prevMillis;
        chatting$prevMillis = current;
        updatePercentage(diff);
        float t = chatting$percentComplete;
        chatting$animationPercent = MathUtils.clamp(1 - (--t) * t * t * t, 0, 1);
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

    @ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"), index = 3)
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
        if (ChatTabs.INSTANCE.getHasCancelledAnimation()) {
            ChatTabs.INSTANCE.setHasCancelledAnimation(false);
            return;
        }
        chatting$percentComplete = 0;
    }

    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<IChatComponent> setNewLines(List<IChatComponent> original) {
        chatting$newLines = original.size() - 1;
        return original;
    }
}
