package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.chat.RoundedChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Draws styled backgrounds alongside the vanilla line pass. The native call is
 * kept intact so other chat mods can redirect it without a redirect conflict.
 */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Background {
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Shadow private int scrollPos;
    @Shadow public abstract int getLineCount();
    @Shadow public abstract float getChatScale();
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract boolean getChatOpen();

    @Inject(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0, shift = At.Shift.BEFORE)
    )
    private void chatting$drawLineBackgrounds(int updateCounter, CallbackInfo ci) {
        if (!ChattingConfig.INSTANCE.getRoundedChatCorners()) return;
        if (mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) return;

        int visibleLines = Math.min(getLineCount(), Math.max(0, drawnChatLines.size() - scrollPos));
        int firstVisibleBackground = -1;
        int lastVisibleBackground = -1;
        boolean keepMessagesVisible = !ChattingConfig.INSTANCE.getFade() || getChatOpen();
        float opacity = mc.gameSettings.chatOpacity * 0.9F + 0.1F;

        for (int lineIndex = 0; lineIndex < visibleLines; lineIndex++) {
            ChatLine line = drawnChatLines.get(lineIndex + scrollPos);
            int age = chatting$fadeAge(updateCounter - line.getUpdatedCounter());
            if (age >= 200 && !keepMessagesVisible) continue;

            double fade = 1.0D - age / 200.0D;
            fade *= 10.0D;
            fade = Math.max(0.0D, Math.min(1.0D, fade));
            fade *= fade;
            int alpha = keepMessagesVisible ? 255 : (int) (255.0D * fade);
            alpha = (int) (alpha * opacity);
            if (alpha <= 3) continue;

            if (firstVisibleBackground < 0) firstVisibleBackground = lineIndex;
            lastVisibleBackground = lineIndex;
        }

        if (firstVisibleBackground < 0) return;

        int width = (int) Math.ceil(getChatWidth() / getChatScale());
        for (int lineIndex = firstVisibleBackground; lineIndex <= lastVisibleBackground; lineIndex++) {
            ChatLine line = drawnChatLines.get(lineIndex + scrollPos);
            int age = chatting$fadeAge(updateCounter - line.getUpdatedCounter());
            if (age >= 200 && !keepMessagesVisible) continue;

            double fade = 1.0D - age / 200.0D;
            fade *= 10.0D;
            fade = Math.max(0.0D, Math.min(1.0D, fade));
            fade *= fade;
            int alpha = keepMessagesVisible ? 255 : (int) (255.0D * fade);
            alpha = (int) (alpha * opacity);
            if (alpha <= 3) continue;

            int top = -lineIndex * 9;
            int color = ChatBackground.tint(alpha / 2 << 24);
            if (mc.currentScreen instanceof GuiChat && chatting$hovered(0, top - 9, width, top)) {
                color = ChatBackground.tint(alpha / 2 << 24, ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb());
            }
            RoundedChat.fill(0, top - 9, width, top, color, lineIndex == lastVisibleBackground, lineIndex == firstVisibleBackground);
        }
    }

    @ModifyArgs(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0)
    )
    private void chatting$styleVanillaLineBackground(Args args) {
        int vanillaColor = args.get(4);
        if (ChattingConfig.INSTANCE.getRoundedChatCorners()) {
            args.set(4, vanillaColor & 0x00FFFFFF);
            return;
        }

        int left = args.get(0);
        int top = args.get(1);
        int right = args.get(2);
        int bottom = args.get(3);
        int color = ChatBackground.tint(vanillaColor);
        if (mc.currentScreen instanceof GuiChat && chatting$hovered(left, top, right, bottom)) {
            color = ChatBackground.tint(vanillaColor, ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb());
        }
        args.set(4, color);
    }

    private int chatting$fadeAge(int age) {
        return age + 200 - (int) (ChattingConfig.INSTANCE.getFadeTime() * 20f);
    }

    private boolean chatting$hovered(int left, int top, int right, int bottom) {
        ScaledResolution resolution = new ScaledResolution(mc);
        float chatScale = getChatScale();
        if (chatScale <= 0f) return false;

        float mouseX = (float) Mouse.getX() / resolution.getScaleFactor();
        float mouseY = resolution.getScaledHeight() - (float) Mouse.getY() / resolution.getScaleFactor();
        float x1 = 2f + left * chatScale;
        float x2 = 2f + right * chatScale;
        float chatBottom = resolution.getScaledHeight() - 40f;
        float y1 = chatBottom + top * chatScale;
        float y2 = chatBottom + bottom * chatScale;
        return mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
    }
}
