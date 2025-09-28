package org.polyfrost.chatting.mixin;

import dev.deftu.textile.minecraft.MCTextHolder;
import org.polyfrost.chatting.util.McChat;
import org.polyfrost.chatting.util.MessageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if FORGE
//$$ @Mixin(net.minecraft.client.gui.GuiNewChat.class)
//#else
@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
//#endif
public class ChatMixin {

    //#if MC <= 11202
    //#if FORGE
    //$$ @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    //$$ private void newMessage(net.minecraft.util.IChatComponent message, int chatLineId, CallbackInfo ci) {
    //#else
    //$$ @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"))
    //$$ private void addMessage(net.minecraft.text.Text message, int messageId, CallbackInfo ci) {
    //#endif
    //$$     MessageInfo messageInfo = new MessageInfo(MCTextHolder.convertFromVanilla(message), null);
    //$$     McChat.INSTANCE.addMessage(messageInfo);
    //$$ }
    //#else
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"))
    private void newMessage(net.minecraft.text.Text text, net.minecraft.network.message.MessageSignatureData messageSignatureData, net.minecraft.client.gui.hud.MessageIndicator messageIndicator, CallbackInfo ci) {
        MessageInfo messageInfo = new MessageInfo(MCTextHolder.convertFromVanilla(text), messageIndicator);
        McChat.INSTANCE.addMessage(messageInfo);
    }
    //#endif
}
