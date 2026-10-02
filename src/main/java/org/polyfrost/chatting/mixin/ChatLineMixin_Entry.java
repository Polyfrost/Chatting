package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.hook.ChatEntryHook;
import org.polyfrost.chatting.hook.ChatHeadState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/^* Pairs each wrapped chat line with the message it was split from. ^/
@Mixin(ChatLine.class)
public class ChatLineMixin_Entry implements ChatEntryHook {
    @Unique private IChatComponent chatting$parent;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void chatting$captureParent(int updateCounter, IChatComponent component, int chatLineId, CallbackInfo ci) {
        // currentComponent is the unwrapped message vanilla is splitting. It is
        // null for lines built outside setChatLine, where the line is its own entry.
        chatting$parent = ChatHeadState.currentComponent != null ? ChatHeadState.currentComponent : component;
    }

    @Override
    public IChatComponent chatting$getParent() {
        return chatting$parent;
    }
}
*///?}
