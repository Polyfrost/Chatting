package org.polyfrost.chatting.hook;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*//~ if =1.8.9 'net.minecraft.client.GuiMessage' -> 'net.minecraft.client.gui.ChatMessage'
import net.minecraft.client.GuiMessage;
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
    //~ if =1.8.9 'GuiMessage' -> 'ChatMessage'
    GuiMessage chatting$getParent();

    //~ if =1.8.9 'GuiMessage' -> 'ChatMessage'
    void chatting$setParent(@Nullable GuiMessage parent);
    //? if = 1.8.9 {

    /*boolean chatting$isEndOfEntry();

    void chatting$setEndOfEntry(boolean endOfEntry);
    *///?}
}
