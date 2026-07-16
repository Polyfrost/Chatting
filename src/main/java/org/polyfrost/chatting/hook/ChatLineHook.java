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

    /**
     * The {@link GuiMessage} this line was wrapped from. On 26.1+ this is the line record's native
     * {@code parent} field; on 1.21.11 and below it is a field added by {@code GuiMessageLineMixin}.
     * Pairing a line back to its message by reference avoids the {@link java.util.List#indexOf}
     * pitfall that {@code GuiMessage.Line} being a record creates for duplicate messages.
     */
    @Nullable
    GuiMessage chatting$getParent();

    void chatting$setParent(@Nullable GuiMessage parent);
}
