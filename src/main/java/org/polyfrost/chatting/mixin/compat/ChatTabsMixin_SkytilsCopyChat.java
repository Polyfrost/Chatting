package org.polyfrost.chatting.mixin.compat;

import cc.polyfrost.oneconfig.utils.Notifications;
import net.minecraft.client.gui.GuiScreen;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "gg.skytils.skytilsmod.features.impl.handlers.ChatTabs")
public class ChatTabsMixin_SkytilsCopyChat {

    @Unique
    private static long chatting$lastNotify = System.currentTimeMillis();

    @Dynamic("Skytils")
    @Redirect(method = "onAttemptCopy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;isCtrlKeyDown()Z"))
    private boolean onAttemptCopy() {
        boolean isCtrlKeyDown = GuiScreen.isCtrlKeyDown();
        if (!ChattingConfig.INSTANCE.getRightClickCopy() && isCtrlKeyDown) {
            if (System.currentTimeMillis() - chatting$lastNotify >= 1000) {
                Notifications.INSTANCE.send("Chatting", "Skytils' Copy Chat has been replaced by Chatting. You can configure this via OneConfig, by clicking the right shift key on your keyboard, or by typing /chatting in your chat.");
                chatting$lastNotify = System.currentTimeMillis();
            }
        }
        return false;
    }

    @Dynamic("Skytils")
    @Redirect(method = "onAttemptCopy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;isShiftKeyDown()Z"))
    private boolean onAttemptCopyShift() {
        boolean isShiftKeyDown = GuiScreen.isShiftKeyDown();
        if (!ChattingConfig.INSTANCE.getRightClickCopy() && isShiftKeyDown) {
            if (System.currentTimeMillis() - chatting$lastNotify >= 1000) {
                Notifications.INSTANCE.send("Chatting", "Skytils' Copy Chat has been replaced by Chatting. You can configure this via OneConfig, by clicking the right shift key on your keyboard, or by typing /chatting in your chat.");
                chatting$lastNotify = System.currentTimeMillis();
            }
        }
        return false;
    }
}
