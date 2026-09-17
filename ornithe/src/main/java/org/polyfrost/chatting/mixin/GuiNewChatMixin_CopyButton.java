package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatCopyButton;
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

/** Renders the focused-chat copy affordance beside the line currently under the cursor. */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_CopyButton extends Gui {
    private static final ResourceLocation CHATTING$COPY = new ResourceLocation("chatting", "copy.png");

    @Shadow @Final private Minecraft mc;
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract float getChatScale();

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$resetCopyHover(int updateCounter, CallbackInfo ci) {
        ChatCopyButton.reset();
    }

    @ModifyArgs(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I")
    )
    private void chatting$drawCopyButton(Args args) {
        if (!ChattingConfig.INSTANCE.getChatCopy() || !(mc.currentScreen instanceof GuiChat)) return;

        int top = (int) ((float) args.get(2) - 1f);
        int left = (int) Math.ceil(getChatWidth() / getChatScale()) + 5;
        int right = left + 9;
        if (!chatting$copyHovered(left, top, right, top + 9)) return;

        int background = ChattingConfig.INSTANCE.getChatButtonHoveredBackgroundColor().getArgb();
        drawRect(left, top, right, top + 9, background);
        mc.getTextureManager().bindTexture(CHATTING$COPY);
        drawModalRectWithCustomSizedTexture(left, top, 0f, 0f, 9, 9, 9f, 9f);
        ChatCopyButton.hover((String) args.get(0));
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
