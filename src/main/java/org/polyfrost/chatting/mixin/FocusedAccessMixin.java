package org.polyfrost.chatting.mixin;

//? if >=1.21.11 {
/*import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if <26 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/^import net.minecraft.client.gui.GuiGraphicsExtractor;
^///?}

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess")
public class FocusedAccessMixin {

    @Unique private int chatting$mouseX;
    @Unique private int chatting$mouseY;

    //? if <26 {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void chatting$captureMouse(GuiGraphics graphics, Font font, int mouseX, int mouseY, boolean changeCursor, CallbackInfo ci) {
        this.chatting$mouseX = mouseX;
        this.chatting$mouseY = mouseY;
    }

    @Redirect(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    private void chatting$hoverFill(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int chatting$ex2 = x2 + org.polyfrost.chatting.chat.ChatButtons.extraBackgroundWidth();
        graphics.fill(x1, y1, chatting$ex2, y2, chatting$hoverColor(graphics.pose(), x1, y1, x2, y2, color));
    }

    @Unique
    private int chatting$hoverColor(org.joml.Matrix3x2fStack pose, int x1, int y1, int x2, int y2, int color) {
        if (!chatting$chatFocused()) return color;
        Vector2f m = pose.invert(new Matrix3x2f()).transformPosition(chatting$mouseX, chatting$mouseY, new Vector2f());
        if (m.x >= x1 && m.x < x2 && m.y >= y1 && m.y < y2) {
            return ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        }
        return color;
    }
    //?} else {
    /^@Inject(method = "<init>", at = @At("TAIL"))
    private void chatting$captureMouse(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, boolean changeCursor, CallbackInfo ci) {
        this.chatting$mouseX = mouseX;
        this.chatting$mouseY = mouseY;
    }

    @Redirect(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void chatting$hoverFill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        int chatting$ex2 = x2 + org.polyfrost.chatting.chat.ChatButtons.extraBackgroundWidth();
        graphics.fill(x1, y1, chatting$ex2, y2, chatting$hoverColor(graphics.pose(), x1, y1, x2, y2, color));
    }

    @Unique
    private int chatting$hoverColor(org.joml.Matrix3x2fStack pose, int x1, int y1, int x2, int y2, int color) {
        if (!chatting$chatFocused()) return color;
        Vector2f m = pose.invert(new Matrix3x2f()).transformPosition(chatting$mouseX, chatting$mouseY, new Vector2f());
        if (m.x >= x1 && m.x < x2 && m.y >= y1 && m.y < y2) {
            return ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        }
        return color;
    }
    ^///?}

    @Unique
    private boolean chatting$chatFocused() {
        //? if >=26.2 {
        /^return Minecraft.getInstance().gui.screen() instanceof net.minecraft.client.gui.screens.ChatScreen;
        ^///?} else {
        return Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.ChatScreen;
        //?}
    }
}
*///?}
//? if <1.21.11 {
public class FocusedAccessMixin {
}
//?}
