package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.RoundedChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/^*
 * Draws styled backgrounds alongside the vanilla line pass. The native call is
 * kept intact so other chat mods can redirect it without a redirect conflict.
 ^/
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Background {
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Shadow private int scrollPos;
    @Shadow public abstract int getLineCount();
    @Shadow public abstract float getChatScale();
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract boolean getChatOpen();

    @ModifyArgs(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0)
    )
    private void chatting$styleVanillaLineBackground(Args args, int updateCounter) {
        int vanillaColor = args.get(4);
        if (ChattingConfig.INSTANCE.getRoundedChatCorners()) {
            int left = args.get(0);
            int top = args.get(1);
            int right = (int) args.get(2) + chatting$buttonBackgroundWidth();
            int bottom = args.get(3);
            int color = ChatBackground.tint(vanillaColor);
            if (mc.currentScreen instanceof GuiChat && chatting$hovered(left, top, right, bottom)) {
                color = ChatBackground.tint(vanillaColor, ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb());
            }
            int lineIndex = -bottom / 9;
            int[] bounds = chatting$roundedBounds(updateCounter);
            RoundedChat.fill(left, top, right, bottom, color, lineIndex == bounds[1], lineIndex == bounds[0]);
            args.set(2, right);
            args.set(4, vanillaColor & 0x00FFFFFF);
            return;
        }

        int left = args.get(0);
        int top = args.get(1);
        int right = (int) args.get(2) + chatting$buttonBackgroundWidth();
        int bottom = args.get(3);
        args.set(2, right);
        int color = ChatBackground.tint(vanillaColor);
        if (mc.currentScreen instanceof GuiChat && chatting$hovered(left, top, right, bottom)) {
            color = ChatBackground.tint(vanillaColor, ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb());
        }
        // Match the rounded path: render our configured background explicitly,
        // then keep vanilla's rectangle as a transparent compatibility call.
        // Letting vanilla paint the visible square directly is the one path
        // that makes 3D chat heads appear dark.
        RoundedChat.fill(left, top, right, bottom, color, false, false);
        args.set(4, vanillaColor & 0x00FFFFFF);
    }

    // Gui.drawRect leaves its RGBA colour active.  Chat heads are rendered
    // immediately after this call, so restore the normal GUI colour at the
    // background boundary instead of relying on each later renderer to do it.
    @Inject(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void chatting$restoreColorAfterLineBackground(int updateCounter, CallbackInfo ci) {
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Unique
    private int[] chatting$roundedBounds(int updateCounter) {
        int visibleLines = Math.min(getLineCount(), Math.max(0, drawnChatLines.size() - scrollPos));
        int first = -1;
        int last = -1;
        boolean keepMessagesVisible = !ChattingConfig.INSTANCE.getFade() || getChatOpen();
        float opacity = mc.gameSettings.chatOpacity * 0.9F + 0.1F;

        for (int lineIndex = 0; lineIndex < visibleLines; lineIndex++) {
            ChatLine line = drawnChatLines.get(lineIndex + scrollPos);
            int age = chatting$fadeAge(updateCounter - line.getUpdatedCounter());
            if (age >= 200 && !keepMessagesVisible) continue;

            double fade = 1.0D - age / 200.0D;
            fade = Math.max(0.0D, Math.min(1.0D, fade * 10.0D));
            int alpha = keepMessagesVisible ? 255 : (int) (255.0D * fade * fade);
            if ((int) (alpha * opacity) <= 3) continue;
            if (first < 0) first = lineIndex;
            last = lineIndex;
        }
        return new int[]{first, last};
    }

    @Unique
    private int chatting$buttonBackgroundWidth() {
        if (!ChattingConfig.INSTANCE.getExtendBG() || !(mc.currentScreen instanceof GuiChat)) return 0;

        return ChatButtons.extraBackgroundWidth();
    }

    @Unique
    private int chatting$fadeAge(int age) {
        return age + 200 - (int) (ChattingConfig.INSTANCE.getFadeTime() * 20f);
    }

    @Unique
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
*///?}
