package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class GameSettings_ChatScaleChangeEvent_Mixin {
    @Shadow
    protected Minecraft mc;

    @Inject(method = "setOptionFloatValue", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;chatScale:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void onChatScaleChange(GameSettings.Options settingsOption, float value, CallbackInfo ci) {
        Chatting.INSTANCE.getChatWindow().updateMCChatScale();
    }
}
