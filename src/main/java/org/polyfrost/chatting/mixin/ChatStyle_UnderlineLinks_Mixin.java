package org.polyfrost.chatting.mixin;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatStyle.class)
public abstract class ChatStyle_UnderlineLinks_Mixin {
    @Shadow
    private ClickEvent chatClickEvent;

    @Shadow
    public abstract ChatStyle setUnderlined(Boolean underlined);

    @Shadow
    public abstract ChatStyle setColor(EnumChatFormatting color);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void underLineAndHighlightLinks(CallbackInfo ci) {
        if (chatClickEvent != null && chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL && ChattingConfig.INSTANCE.getUnderlinedLinks()) {
            setUnderlined(true);
            setColor(EnumChatFormatting.BLUE);
        }
    }
}
