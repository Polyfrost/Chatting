package org.polyfrost.chatting.hook;

//? if = 1.8.9 {
/*import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;

/^* Per-message context supplied while vanilla wraps an incoming chat component. ^/
public final class ChatHeadState {
    private ChatHeadState() {
    }

    public static IChatComponent currentComponent;
    public static boolean lineVisible;
    public static NetworkPlayerInfo lastPlayerInfo;

    /^* Starts a fresh visible-message sequence before vanilla rebuilds chat lines. ^/
    public static void resetConsecutiveTracking() {
        lastPlayerInfo = null;
    }
}
*///?}
