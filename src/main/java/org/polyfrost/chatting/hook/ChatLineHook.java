package org.polyfrost.chatting.hook;

import net.minecraft.client.multiplayer.PlayerInfo;
import org.jetbrains.annotations.Nullable;

public interface ChatLineHook {

    @Nullable
    PlayerInfo chatting$getPlayerInfo();

    void chatting$setPlayerInfo(@Nullable PlayerInfo info);

    boolean chatting$isHeadHidden();

    void chatting$setHeadHidden(boolean hidden);
}
