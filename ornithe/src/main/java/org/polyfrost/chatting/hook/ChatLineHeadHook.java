package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public interface ChatLineHeadHook {
    Set<WeakReference<ChatLine>> LINES = new HashSet<>();

    boolean chatting$hasDetectedPlayer();

    NetworkPlayerInfo chatting$getPlayerInfo();

    void chatting$updatePlayerInfo();
}
