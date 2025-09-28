package org.polyfrost.chatting.mixin;

import org.jetbrains.annotations.Nullable;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.polyui.data.PolyImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//#if FORGE
//$$ @Mixin(net.minecraft.client.gui.ChatLine.class)
//#else
@Mixin(net.minecraft.client.gui.hud.ChatHudLine.class)
//#endif
public class ChatLineMixin implements ChatLineHook {

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