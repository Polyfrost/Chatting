package org.polyfrost.chatting.mixin;

import dev.deftu.omnicore.client.render.OmniMatrixStack;
import org.polyfrost.chatting.component.ChatComponent;
import org.polyfrost.chatting.component.ChatLineComponent;
import org.polyfrost.oneconfig.api.platform.v1.internal.ScreenPlatformImpl;
import org.polyfrost.polyui.component.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ScreenPlatformImpl.class, remap = false)
public class ScreenPlatformImplMixin {

    @Shadow
    private OmniMatrixStack smuggled;

@ModifyVariable(method = "renderLegacyHuds", at = @At(value = "STORE", ordinal = 0))
    private Component renderLegacy(Component component) {
        if (component instanceof ChatComponent chatComponent) {
            if (chatComponent.getChildren() != null) {
                for (Component child : chatComponent.getChildren()) {
                    if (child instanceof ChatLineComponent chatLineComponent) {
                        chatLineComponent.renderLegacy(smuggled);
                    }
                }
            }
        }
        return component;
    }
}