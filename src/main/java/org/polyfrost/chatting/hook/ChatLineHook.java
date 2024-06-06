package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public interface ChatLineHook {
    HashSet<WeakReference<ChatLine>> chatting$chatLines = new HashSet<>();
    boolean chatting$hasDetected();
    NetworkPlayerInfo chatting$getPlayerInfo();

    void chatting$updatePlayerInfo();

    long chatting$getUniqueId();

    ChatLine getFullMessage();
}
