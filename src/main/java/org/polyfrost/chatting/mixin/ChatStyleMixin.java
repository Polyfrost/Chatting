package org.polyfrost.chatting.mixin;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatStyle.class)
public class ChatStyleMixin {
    @Shadow
    private ClickEvent chatClickEvent;

    @Unique
    private boolean chatting$hasURL() {
        return ChattingConfig.INSTANCE.getUnderlinedLinks()
            && chatClickEvent != null
            && chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL;
    }

    @Inject(method = "getUnderlined", at = @At("HEAD"), cancellable = true)
    private void linkUnderline(CallbackInfoReturnable<Boolean> cir) {
        if (chatting$hasURL()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void linkColor(CallbackInfoReturnable<EnumChatFormatting> cir) {
        if (chatting$hasURL()) {
            cir.setReturnValue(EnumChatFormatting.BLUE);
        }
    }
}
