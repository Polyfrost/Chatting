package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.libs.universal.UMinecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiNewChat.class, priority = 1100)
public class GuiNewChatMixin_ChatPeek {

    @Inject(method = "getChatOpen", at = @At("HEAD"), cancellable = true)
    private void chatPeek(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(UMinecraft.getMinecraft().currentScreen instanceof GuiChat || Chatting.INSTANCE.getPeeking());
    }

}