package org.polyfrost.chatting.chat;

//? if > 1.8.9 {
import org.polyfrost.chatting.config.ChattingConfig;

public final class ChatBackground {

    private ChatBackground() {
    }

    // scales the configured alpha by the vanilla one so opacity settings and message fading still apply
    public static int tint(int color) {
        int configured = ChattingConfig.INSTANCE.getChatBackgroundColor().getArgb();
        int alpha = (color >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }
}
//?} else {
/*import org.polyfrost.chatting.config.ChattingConfig;

/^* Applies Chatting's configured color without bypassing vanilla opacity and fade. ^/
public final class ChatBackground {
    private ChatBackground() {
    }

    public static int tint(int vanillaColor) {
        return tint(vanillaColor, ChattingConfig.INSTANCE.getChatBackgroundColor().getArgb());
    }

    public static int tint(int vanillaColor, int configured) {
        int alpha = (vanillaColor >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }
}
*///?}
