package org.polyfrost.chatting.mixin.compat;

import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.oneconfig.api.notifications.v1.Notifications;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"at.hannibal2.skyhanni.features.chat.ChatPeek"})
public class ChatPeekMixin_SkyHanni {

    @Unique
    private static long chatting$lastNotify = System.currentTimeMillis();

    @Dynamic("SkyHanni")
    @Inject(method = "peek", at = @At("RETURN"), cancellable = true)
    private static void cancel(CallbackInfoReturnable<Boolean> cir) {
        if (!ChattingConfig.INSTANCE.getChatPeek() && cir.getReturnValue()) {
            if (System.currentTimeMillis() - chatting$lastNotify >= 1000) {
                Notifications.info("Chatting", "SkyHanni's chat peek has been replaced by Chatting. Configure it through OneConfig.");
                chatting$lastNotify = System.currentTimeMillis();
            }
        }
        cir.setReturnValue(false);
    }
}
