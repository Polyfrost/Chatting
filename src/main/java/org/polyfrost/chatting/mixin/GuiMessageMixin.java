package org.polyfrost.chatting.mixin;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}
import org.polyfrost.chatting.hook.ChatMessageHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
//? if >=26 {
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.polyfrost.chatting.chat.ChatTimestamps;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
//?}

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements ChatMessageHook {

    @Unique
    private long chatting$timestamp = -1L;

    @Override
    public long chatting$getTimestamp() {
        return chatting$timestamp;
    }

    @Override
    public void chatting$setTimestamp(long millis) {
        this.chatting$timestamp = millis;
    }

    //? if >=26 {
    @ModifyArg(method = "splitLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;"), index = 0)
    private FormattedText chatting$prependTimestamp(FormattedText content) {
        return ChatTimestamps.INSTANCE.prepend(this.chatting$timestamp, (Component) content);
    }
    //?}
}
