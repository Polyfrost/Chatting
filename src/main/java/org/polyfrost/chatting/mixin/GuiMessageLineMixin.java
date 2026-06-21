package org.polyfrost.chatting.mixin;

//? if >=26 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;*/
//?} else {
import net.minecraft.client.GuiMessage;
//?}
import net.minecraft.client.multiplayer.PlayerInfo;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** (see {@link ChatLineHook}) */
@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin implements ChatLineHook {

    @Unique
    @Nullable
    private PlayerInfo chatting$playerInfo;

    @Unique
    private boolean chatting$headHidden;

    @Override
    @Nullable
    public PlayerInfo chatting$getPlayerInfo() {
        return chatting$playerInfo;
    }

    @Override
    public void chatting$setPlayerInfo(@Nullable PlayerInfo info) {
        this.chatting$playerInfo = info;
    }

    @Override
    public boolean chatting$isHeadHidden() {
        return chatting$headHidden;
    }

    @Override
    public void chatting$setHeadHidden(boolean hidden) {
        this.chatting$headHidden = hidden;
    }
}
