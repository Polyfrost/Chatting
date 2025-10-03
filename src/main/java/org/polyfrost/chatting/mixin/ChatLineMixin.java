package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.polyui.data.PolyImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.class)
public class ChatLineMixin implements ChatLineHook {

    @Unique
    private PolyImage chatting$head;

    public PolyImage chatting$getChatHead() {
        return chatting$head;
    }

    public void chatting$setChatHead(PolyImage chatting$head) {
        this.chatting$head = chatting$head;
    }
}