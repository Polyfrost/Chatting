package org.polyfrost.chatting.hook;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}
import net.minecraft.client.multiplayer.PlayerInfo;
import org.jetbrains.annotations.Nullable;

public interface ChatLineHook {

    @Nullable
    PlayerInfo chatting$getPlayerInfo();

    void chatting$setPlayerInfo(@Nullable PlayerInfo info);

    boolean chatting$isHeadHidden();

    void chatting$setHeadHidden(boolean hidden);

    // pairing by reference avoids the List#indexOf pitfall that GuiMessage.Line being a record creates for duplicate messages
    @Nullable
    GuiMessage chatting$getParent();

    void chatting$setParent(@Nullable GuiMessage parent);
}
