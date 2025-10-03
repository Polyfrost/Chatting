package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.polyfrost.chatting.util.McChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public class ChatMixin {

    //#if MC > 1.16.5
    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At("HEAD"))
    private void onAdd(ChatHudLine chatHudLine, CallbackInfo ci) {
        McChat.INSTANCE.addMessage(chatHudLine);
    }
    //#else
    //$$ @org.spongepowered.asm.mixin.Shadow
    //$$ @Final
    //$$ private List<ChatHudLine> messages;
    //$$ @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("TAIL"))
    //$$ private void onAdd(net.minecraft.text.Text text, int i, int j, boolean bl, CallbackInfo ci) {
    //$$     if (!bl) {
    //$$         McChat.INSTANCE.addMessage(this.messages.get(0));
    //$$     }
    //$$ }
    //#endif

    @Inject(method = "clear", at = @At("HEAD"))
    private void onClear(CallbackInfo ci) {
        McChat.INSTANCE.clearMessages();
    }
}
