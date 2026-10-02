package org.polyfrost.chatting.chat;

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
