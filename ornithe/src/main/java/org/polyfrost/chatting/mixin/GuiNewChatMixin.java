package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hook.GuiNewChatHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Fabric (Ornithe) equivalent of the vanilla-facing Chatting hooks.  Position
 * and scaling stay with vanilla 1.8.9; the retired PolyUI HUD is deliberately
 * not part of this render path.
 */
@Mixin(value = GuiNewChat.class, priority = 990)
public abstract class GuiNewChatMixin implements GuiNewChatHook {
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Shadow private int scrollPos;
    @Shadow public abstract boolean getChatOpen();
    @Shadow public abstract int getLineCount();
    @Shadow public abstract int getChatWidth();
    @Shadow public abstract float getChatScale();

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void chatting$checkScreenshotKeybind(int updateCounter, CallbackInfo ci) {
        if (Chatting.INSTANCE.getKeybind().isPressed()) Chatting.INSTANCE.setDoTheThing(true);
    }

    @Override
    public int chatting$getRight() {
        return getChatWidth();
    }

    @Override
    public boolean chatting$isHovering() {
        return getChatOpen();
    }

    @Override
    public ChatLine chatting$getHoveredLine(int mouseY) {
        if (!getChatOpen()) return null;
        ScaledResolution resolution = new ScaledResolution(mc);
        int scaledMouseY = mouseY / resolution.getScaleFactor();
        int row = MathHelper.floor_float((resolution.getScaledHeight() - scaledMouseY - 40) / getChatScale());
        if (row < 0) return null;
        int line = row / mc.fontRendererObj.FONT_HEIGHT + scrollPos;
        return line >= 0 && line < drawnChatLines.size() && line < getLineCount() ? drawnChatLines.get(line) : null;
    }

    @Override
    public String chatting$getChattingChatComponent(int mouseY, int mouseButton) {
        ChatLine line = chatting$getHoveredLine(mouseY);
        if (line == null) return null;
        if (GuiScreen.isShiftKeyDown() && mouseButton == 0) return null;
        ChatLine fullLine = ((ChatLineHook) line).chatting$getFullMessage();
        ChatLine selected = GuiScreen.isCtrlKeyDown() && mouseButton == 0 ? line : fullLine;
        String message = (selected == null ? line : selected).getChatComponent().getFormattedText();
        return GuiScreen.isAltKeyDown() ? message : EnumChatFormatting.getTextWithoutFormattingCodes(message);
    }

    @Override
    public int chatting$getTextOpacity() {
        return 255;
    }
}
