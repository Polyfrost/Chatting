package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.utils.Notifications;
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

    @Unique private boolean chatting$said = false;

    @Dynamic("Essential")
    @Inject(method = "isHoldingChatPeek", at = @At("RETURN"), cancellable = true)
    private void overrideChatPeek(CallbackInfoReturnable<Boolean> cir) {
        if (!chatting$said && cir.getReturnValue()) {
            Notifications.INSTANCE.send("Chatting", "Essential's chat peek has been replaced by Chatting, /chatting to access config GUI.");
            chatting$said = true;
        }
        cir.setReturnValue(false);
    }

}