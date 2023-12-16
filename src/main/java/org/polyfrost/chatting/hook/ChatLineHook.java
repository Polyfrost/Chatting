package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;

public interface ChatLineHook {
    HashSet<WeakReference<ChatLine>> chatLines = new HashSet<>();
    boolean isDetected();
    void setDetected(boolean detected);
    List<ChatLine> getChildren();
    long getTimestamp();
    void setTimestamp(long timestamp);
    NetworkPlayerInfo getPlayerInfo();
    void setPlayerInfo(NetworkPlayerInfo playerInfo);
    NetworkPlayerInfo getDetectedPlayerInfo();
    void setDetectedPlayerInfo(NetworkPlayerInfo detectedPlayerInfo);
    boolean isFirstDetection();
    void setFirstDetection(boolean firstDetection);

    void updatePlayerInfo();

    long getUniqueId();
}
