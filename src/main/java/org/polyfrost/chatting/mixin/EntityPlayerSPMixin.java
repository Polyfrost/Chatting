package org.polyfrost.chatting.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.polyfrost.chatting.chat.ChatTab;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityPlayerSP.class, priority = 0)
public class EntityPlayerSPMixin {
    @Shadow @Final public NetHandlerPlayClient sendQueue;

    @Redirect(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V"))
    private void handleSentMessages(NetHandlerPlayClient instance, Packet<?> packet, String value) {
        if (value.startsWith("/")) {
            this.sendQueue.addToSendQueue(packet);
            return;
        }
        if (ChattingConfig.INSTANCE.getChatTabs() && !ChatTabs.INSTANCE.getCurrentTabs().isEmpty()) {
            boolean sent = false;
            for (ChatTab tab : ChatTabs.INSTANCE.getCurrentTabs()) {
                if (tab.getPrefix() != null && !tab.getPrefix().isEmpty()) {
                    this.sendQueue.addToSendQueue(new C01PacketChatMessage(tab.getPrefix() + value));
                    sent = true;
                }
            }
            if (!sent) {
                this.sendQueue.addToSendQueue(packet);
            }
        } else {
            this.sendQueue.addToSendQueue(packet);
        }
    }
}