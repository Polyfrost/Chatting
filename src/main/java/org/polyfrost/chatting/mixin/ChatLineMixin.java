package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.polyfrost.chatting.component.PlayerHead;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.class)
public class ChatLineMixin implements ChatLineHook {

    @Unique
    private PlayerHead chatting$head;

    public PlayerHead chatting$getChatHead() {
        return chatting$head;
    }

    public void chatting$setChatHead(PlayerHead chatting$head) {
        this.chatting$head = chatting$head;
    }
}