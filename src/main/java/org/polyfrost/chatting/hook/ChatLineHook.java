package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public interface ChatLineHook {
    HashSet<WeakReference<ChatLine>> chatLines = new HashSet<>();
    boolean hasDetected();
    NetworkPlayerInfo getPlayerInfo();

    void updatePlayerInfo();

    long getUniqueId();
}
