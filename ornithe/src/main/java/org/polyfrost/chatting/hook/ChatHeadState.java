package org.polyfrost.chatting.hook;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;

/** Per-message context supplied while vanilla wraps an incoming chat component. */
public final class ChatHeadState {
    private ChatHeadState() {
    }

    public static IChatComponent currentComponent;
    public static boolean lineVisible;
    public static NetworkPlayerInfo lastPlayerInfo;
}
