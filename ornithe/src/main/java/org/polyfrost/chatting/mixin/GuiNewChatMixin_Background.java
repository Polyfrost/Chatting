package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Styles vanilla's per-line chat fills in place, preserving its opacity and
 * fade calculation. GuiNewChat's local coordinates are transformed from the
 * lower-left chat origin, so the hit test mirrors that transform in GUI space.
 */
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Background {
    @Shadow @Final private Minecraft mc;
    @Shadow public abstract float getChatScale();

    @ModifyArgs(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0)
    )
    private void chatting$styleLineBackground(Args args) {
        int left = args.get(0);
        int top = args.get(1);
        int right = args.get(2);
        int bottom = args.get(3);
        int color = ChatBackground.tint(args.get(4));
        if (mc.currentScreen instanceof GuiChat && chatting$hovered(left, top, right, bottom)) {
            color = ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        }
        args.set(4, color);
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
