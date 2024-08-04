package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.gui.animations.Animation;
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation;
import org.polyfrost.chatting.chat.ChatScrollingHook;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHook;
import org.polyfrost.chatting.utils.EaseOutQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Scrolling extends Gui {
    @Shadow
    private int scrollPos;

    @Unique
    private Animation chatting$scrollingAnimation = new DummyAnimation(0f);

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$scrollingAnimationStart(int updateCounter, CallbackInfo ci) {
        boolean shouldSmooth = ChatScrollingHook.INSTANCE.getShouldSmooth();
        if (shouldSmooth) ChatScrollingHook.INSTANCE.setShouldSmooth(false);
        if (ChattingConfig.INSTANCE.getSmoothScrolling()) {
            if (chatting$scrollingAnimation.getEnd() != scrollPos) {
                if (Math.abs(chatting$scrollingAnimation.getEnd() - scrollPos) > 1 && shouldSmooth) {
                    chatting$scrollingAnimation = new EaseOutQuad((int) (ChattingConfig.INSTANCE.getScrollingSpeed() * 1000), chatting$scrollingAnimation.get(), scrollPos, false);
                } else {
                    chatting$scrollingAnimation = new DummyAnimation(scrollPos);
                }
            }
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiNewChat;scrollPos:I"))
    private int redirectPos(GuiNewChat instance) {
        return ChattingConfig.INSTANCE.getSmoothScrolling() ? (int) chatting$scrollingAnimation.get() : scrollPos;
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 1))
    private void redirectScrollBar(int updateCounter, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getRemoveScrollBar()) ChatHook.cancelRect = true;
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 2))
    private void redirectScrollBar2(int updateCounter, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getRemoveScrollBar()) ChatHook.cancelRect = true;
    }

}
