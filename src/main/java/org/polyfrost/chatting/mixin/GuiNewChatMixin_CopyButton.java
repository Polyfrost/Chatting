package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.ChatCopyButton;
import org.polyfrost.chatting.chat.ChatDeleteButton;
import org.polyfrost.chatting.chat.Textures;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;

/** Renders the focused-chat copy affordance beside the line currently under the cursor. */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_CopyButton extends Gui {
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Shadow private int scrollPos;
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract float getChatScale();

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$resetCopyHover(int updateCounter, CallbackInfo ci) {
        ChatCopyButton.reset();
        ChatDeleteButton.reset();
    }

    @ModifyArgs(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I")
    )
    private void chatting$drawCopyButton(Args args) {
        if (!(mc.currentScreen instanceof GuiChat)) return;

        int top = (int) ((float) args.get(2) - 1f);
        int lineIndex = -(top + 9) / 9;
        int drawnIndex = lineIndex + scrollPos;
        if (drawnIndex < 0 || drawnIndex >= drawnChatLines.size()) return;

        int left = (int) Math.ceil(getChatWidth() / getChatScale()) + 5;
        int stripWidth = ChatButtons.perLineButtonCount() * ChatButtons.BUTTON_SIZE
            + Math.max(0, ChatButtons.perLineButtonCount() - 1) * ChatButtons.BUTTON_GAP;
        // Keep the controls available while moving from the message to the strip;
        // testing only icon bounds made them effectively invisible to the player.
        if (!chatting$copyHovered(-4, top, left + stripWidth, top + ChatButtons.BUTTON_SIZE)) return;

        ChatLine line = drawnChatLines.get(drawnIndex);
        if (ChattingConfig.INSTANCE.getChatCopy()) {
            if (chatting$drawButton(Textures.COPY, left, top)) ChatCopyButton.hover(line);
            left += ChatButtons.BUTTON_SIZE + ChatButtons.BUTTON_GAP;
        }
        if (ChattingConfig.INSTANCE.getChatDelete() && chatting$drawButton(Textures.DELETE, left, top)) {
            ChatDeleteButton.hover(line);
        }
    }

    @Unique
    private boolean chatting$drawButton(ResourceLocation icon, int left, int top) {
        boolean hovered = chatting$copyHovered(left, top, left + ChatButtons.BUTTON_SIZE, top + ChatButtons.BUTTON_SIZE);
        int background = (hovered ? ChattingConfig.INSTANCE.getChatButtonHoveredBackgroundColor()
            : ChattingConfig.INSTANCE.getChatButtonBackgroundColor()).getArgb();
        int color = (hovered ? ChattingConfig.INSTANCE.getChatButtonHoveredColor()
            : ChattingConfig.INSTANCE.getChatButtonColor()).getArgb();
        drawRect(left, top, left + ChatButtons.BUTTON_SIZE, top + ChatButtons.BUTTON_SIZE, background);
        mc.getTextureManager().bindTexture(icon);
        if (ChattingConfig.INSTANCE.getButtonShadow()) {
            GlStateManager.color(0f, 0f, 0f, (color >>> 24) / 255f);
            drawModalRectWithCustomSizedTexture(left + 1, top + 1, 0f, 0f,
                ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE);
        }
        GlStateManager.color(((color >>> 16) & 0xFF) / 255f, ((color >>> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f, (color >>> 24) / 255f);
        drawModalRectWithCustomSizedTexture(left, top, 0f, 0f,
            ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE, ChatButtons.BUTTON_SIZE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        return hovered;
    }

    @Unique
    private boolean chatting$copyHovered(int left, int top, int right, int bottom) {
        ScaledResolution resolution = new ScaledResolution(mc);
        float scale = getChatScale();
        if (scale <= 0f) return false;

        float mouseX = (float) Mouse.getX() / resolution.getScaleFactor();
        float mouseY = resolution.getScaledHeight() - (float) Mouse.getY() / resolution.getScaleFactor();
        float chatBottom = resolution.getScaledHeight() - 40f;
        return mouseX >= 2f + left * scale && mouseX < 2f + right * scale
            && mouseY >= chatBottom + top * scale && mouseY < chatBottom + bottom * scale;
    }
}
