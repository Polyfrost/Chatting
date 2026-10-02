package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.ChatEntries;
import org.polyfrost.chatting.chat.RoundedChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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
            if (mc.currentScreen instanceof GuiChat && chatting$hoveredEntryCovers(bottom)) {
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
        if (mc.currentScreen instanceof GuiChat && chatting$hoveredEntryCovers(bottom)) {
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

    // GuiIngame translates chat by scaledHeight - 48, then GuiNewChat adds 20.
    // Match Forge Chatting's approach of making component selection use the
    // actual rendered origin rather than vanilla's one-pixel-shifted 27.
    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 27))
    private int chatting$alignComponentHitTest(int original) {
        return 28;
    }

    // Highlights every row of the message under the cursor instead of just the
    // wrapped line the cursor sits on. Upstream fix for modern versions:
    // polyfrost/Chatting#167.
    @Unique
    private boolean chatting$hoveredEntryCovers(int bottom) {
        int hovered = chatting$hoveredDrawnIndex();
        if (hovered < 0) return false;

        // drawRect's bottom edge for visible row i is -i * 9
        int row = -bottom / 9;
        int index = row + scrollPos;
        if (index < 0 || index >= drawnChatLines.size()) return false;
        return ChatEntries.sameEntry(drawnChatLines.get(index), drawnChatLines.get(hovered));
    }

    /^* Index into drawnChatLines under the cursor, or -1 when the cursor is off the strip. ^/
    @Unique
    private int chatting$hoveredDrawnIndex() {
        float chatScale = getChatScale();
        if (chatScale <= 0f) return -1;

        ScaledResolution resolution = new ScaledResolution(mc);
        int factor = resolution.getScaleFactor();
        if (!chatting$withinStrip(factor, chatScale)) return -1;

        // Mirror GuiChatMixin's rendered-origin math: the outer HUD translation
        // (-48) plus GuiNewChat's inner one (+20) puts the baseline 28 px up.
        int localY = MathHelper.floor_float((Mouse.getY() / (float) factor - 28f) / chatScale);
        if (localY < 0) return -1;

        // Rows above the rendered page must not match, or hovering just over the
        // chat would highlight a message that is only partly scrolled into view.
        int row = localY / 9;
        if (row >= Math.min(getLineCount(), drawnChatLines.size() - scrollPos)) return -1;
        int index = row + scrollPos;
        return index < drawnChatLines.size() ? index : -1;
    }

    // Spans the background and the per-line button strip, so moving from the
    // message onto copy or delete keeps the entry highlighted.
    @Unique
    private boolean chatting$withinStrip(int factor, float chatScale) {
        int right = (int) Math.ceil(getChatWidth() / chatScale) + 5 + ChatButtons.perLineButtonsWidth();
        int mouseX = Mouse.getX();
        return mouseX >= 2 * factor && mouseX < (int) ((2f + right * chatScale) * factor);
    }
}
*///?}
