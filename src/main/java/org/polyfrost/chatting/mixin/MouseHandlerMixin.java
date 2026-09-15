package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
//? if <1.21.10 {
/*import net.minecraft.client.gui.screens.Screen;
*///?}
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void chatting$peekScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        if (!Chatting.INSTANCE.getPeeking() || !ChattingConfig.INSTANCE.getPeekScrolling()) return;
        Minecraft mc = Minecraft.getInstance();
        //? if >=26.2 {
        if (mc.gui.overlay() != null || mc.gui.screen() != null || mc.player == null) return;
        //?} else {
        /*if (mc.getOverlay() != null || mc.screen != null || mc.player == null) return;
        *///?}
        if (yOffset == 0.0) return;

        int amount = (int) Math.signum(yOffset);
        //? if >=1.21.10 {
        boolean shift = mc.hasShiftDown();
        //?} else {
        /*boolean shift = Screen.hasShiftDown();
        *///?}
        if (!shift) amount *= 7;

        //? if >=26.2 {
        mc.gui.hud.getChat().scrollChat(amount);
        //?} else {
        /*mc.gui.getChat().scrollChat(amount);
        *///?}
        ci.cancel();
    }
}
