package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatCopyButton;
import org.polyfrost.chatting.chat.ChatDeleteButton;
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

/** Renders the focused-chat copy affordance beside the line currently under the cursor. */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_CopyButton extends Gui {
    private static final ResourceLocation CHATTING$COPY = new ResourceLocation("chatting", "copy.png");
    private static final ResourceLocation CHATTING$DELETE = new ResourceLocation("chatting", "delete.png");

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
        int left = (int) Math.ceil(getChatWidth() / getChatScale()) + 5;
        if (ChattingConfig.INSTANCE.getChatCopy() && chatting$copyHovered(left, top, left + 9, top + 9)) {
            chatting$drawButton(CHATTING$COPY, left, top);
            ChatCopyButton.hover((String) args.get(0));
            return;
        }
        left += 10;
        if (!ChattingConfig.INSTANCE.getChatDelete() || !chatting$copyHovered(left, top, left + 9, top + 9)) return;
        int lineIndex = -(top + 9) / 9;
        int drawnIndex = lineIndex + scrollPos;
        if (drawnIndex < 0 || drawnIndex >= drawnChatLines.size()) return;
        chatting$drawButton(CHATTING$DELETE, left, top);
        ChatDeleteButton.hover(drawnChatLines.get(drawnIndex));
    }

    @Unique
    private void chatting$drawButton(ResourceLocation icon, int left, int top) {
        drawRect(left, top, left + 9, top + 9, ChattingConfig.INSTANCE.getChatButtonHoveredBackgroundColor().getArgb());
        mc.getTextureManager().bindTexture(icon);
        drawModalRectWithCustomSizedTexture(left, top, 0f, 0f, 9, 9, 9f, 9f);
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
