package org.polyfrost.chatting.hook;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/^* Per-message context supplied while vanilla wraps an incoming chat component. ^/
public final class ChatHeadState {
    private ChatHeadState() {
    }

    // Player detection below is adapted from chat_heads, licensed MPL-2.0:
    // https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
    private static final Pattern SEPARATOR = Pattern.compile("(§.)|\\W");

    public static IChatComponent currentComponent;
    public static boolean lineVisible;
    public static NetworkPlayerInfo lastPlayerInfo;
    // Head resolved for the message vanilla is about to wrap, or null for none.
    // Needed before the ChatLine instances exist, because the wrap width has to
    // reserve the head's 10 px before splitText runs.
    public static NetworkPlayerInfo pendingHead;

    /^* Starts a fresh visible-message sequence before vanilla rebuilds chat lines. ^/
    public static void resetConsecutiveTracking() {
        lastPlayerInfo = null;
    }

    /^* Finds the player a chat component is attributed to, or null when none matches. ^/
    public static NetworkPlayerInfo detect(IChatComponent source) {
        if (source == null) return null;
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getNetHandler();
        if (connection == null) return null;

        String formatted = source.getFormattedText();
        int colon = formatted.indexOf(':');
        String prefix = EnumChatFormatting.getTextWithoutFormattingCodes(colon >= 0 ? formatted.substring(0, colon) : formatted);
        Map<String, NetworkPlayerInfo> nicknames = new HashMap<>();

        for (String word : SEPARATOR.split(prefix)) {
            if (word.isEmpty()) continue;
            NetworkPlayerInfo info = connection.getPlayerInfo(word);
            if (info == null) info = fromNickname(word, connection, nicknames);
            if (info != null) return info;
        }
        return null;
    }

    private static NetworkPlayerInfo fromNickname(String word, NetHandlerPlayClient connection, Map<String, NetworkPlayerInfo> nicknames) {
        if (nicknames.isEmpty()) {
            for (NetworkPlayerInfo info : connection.getPlayerInfoMap()) {
                IChatComponent displayName = info.getDisplayName();
                if (displayName == null) continue;
                String nickname = EnumChatFormatting.getTextWithoutFormattingCodes(displayName.getFormattedText());
                if (word.equals(nickname)) return info;
                nicknames.put(nickname, info);
            }
        }
        return nicknames.get(word);
    }
}
*///?}
