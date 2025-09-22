package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chatting.hook.ChatHudLineHook;
import org.polyfrost.polyui.data.PolyImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.class)
public class ChatHudLineMixin implements ChatHudLineHook {

    @Unique private PolyImage chatting$head;

    @Override
    public void chatting$setHead(@Nullable PolyImage head) {
        this.chatting$head = head;
    }

    @Override
    public @Nullable PolyImage chatting$getHead() {
        return chatting$head;
    }
}