package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.oneconfig.api.ui.v1.Notifications;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"gg.essential.key.EssentialKeybindingRegistry"})
public class EssentialKeybindingRegistryMixin {

    @Unique
    private long chatting$lastNotify = System.currentTimeMillis();

    @Dynamic("Essential")
    @Inject(method = "isHoldingChatPeek", at = @At("RETURN"), cancellable = true)
    private void overrideChatPeek(CallbackInfoReturnable<Boolean> cir) {
        if (!ChattingConfig.INSTANCE.getChatPeek() && cir.getReturnValue()) {
            if (System.currentTimeMillis() - chatting$lastNotify >= 1000) {
                Notifications.enqueue(Notifications.Type.Info, "Chatting", "Essential's chat peek has been replaced by Chatting. You can configure this via OneConfig, by clicking the right shift key on your keyboard, or by typing /chatting in your chat.");
                chatting$lastNotify = System.currentTimeMillis();
            }
        }
        cir.setReturnValue(false);
    }
}