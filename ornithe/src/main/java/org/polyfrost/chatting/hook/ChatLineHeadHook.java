package org.polyfrost.chatting.hook;

import net.minecraft.client.network.NetworkPlayerInfo;

public interface ChatLineHeadHook {
    boolean chatting$hasDetectedPlayer();

    NetworkPlayerInfo chatting$getPlayerInfo();
}
