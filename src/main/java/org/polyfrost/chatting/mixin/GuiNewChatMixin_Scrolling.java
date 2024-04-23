package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.gui.animations.Animation;
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation;
import cc.polyfrost.oneconfig.utils.MathUtils;
import org.polyfrost.chatting.chat.ChatScrollingHook;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.utils.EaseOutQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Scrolling extends Gui {
    @Shadow
    private int scrollPos;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    private boolean isScrolled;

    @Unique
    private Animation chatting$scrollingAnimation = new DummyAnimation(0f);

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$scrollingAnimationStart(int updateCounter, CallbackInfo ci) {
        if (chatting$scrollingAnimation.isFinished()) {
            if (scrollPos == 0) {
                isScrolled = false;
            }
        } else {
            scrollPos = (int) chatting$scrollingAnimation.get();
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 1))
    private void redirectScrollBar(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) {
            drawRect(left, top, right, bottom, color);
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 2))
    private void redirectScrollBar2(int left, int top, int right, int bottom, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) {
            drawRect(left, top, right, bottom, color);
        }
    }

    @Inject(method = "scroll", at = @At("HEAD"), cancellable = true)
    private void injectScroll(int amount, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getSmoothScrolling() && amount != 0 && ChatScrollingHook.INSTANCE.getShouldSmooth()) {
            ci.cancel();
            ChatScrollingHook.INSTANCE.setShouldSmooth(false);
            int result = (int) MathUtils.clamp(scrollPos + amount, 0, Math.max(drawnChatLines.size() - getLineCount() - 1, 0));
            chatting$scrollingAnimation = new EaseOutQuad((int) (ChattingConfig.INSTANCE.getScrollingSpeed() * 1000), scrollPos, result, false);
        }
    }
}
