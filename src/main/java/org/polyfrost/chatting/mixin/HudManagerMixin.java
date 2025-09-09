package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.event.HudEditorEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudManager.class, remap = false)
public class HudManagerMixin {

    @Shadow
    private static boolean panelExists;

    @Inject(method = "toggleHudPicker", at = @At("TAIL"))
    private void editor(CallbackInfo ci) {
        EventManager.INSTANCE.post(new HudEditorEvent(panelExists));
    }
}
