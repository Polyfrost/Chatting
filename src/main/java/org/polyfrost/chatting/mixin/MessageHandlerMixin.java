package org.polyfrost.chatting.mixin;

//#if MC > 1.16.5
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.polyfrost.chatting.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {

    @Inject(method = "processChatMessageInternal", at = @At("HEAD"))
    private void captureSenderProfile(MessageType.Parameters parameters, SignedMessage signedMessage, Text text, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> cir) {
        Util.currentSender = gameProfile;
    }

    @Inject(method = "processChatMessageInternal", at = @At("RETURN"))
    private void clearSender(MessageType.Parameters parameters, SignedMessage signedMessage, Text text, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> cir) {
        Util.currentSender = null;
    }
}
//#endif