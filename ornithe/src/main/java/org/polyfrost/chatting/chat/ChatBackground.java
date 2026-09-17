package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

/** Applies Chatting's configured color without bypassing vanilla opacity and fade. */
public final class ChatBackground {
    private ChatBackground() {
    }

    public static int tint(int vanillaColor) {
        int configured = ChattingConfig.INSTANCE.getChatBackgroundColor().getArgb();
        int alpha = (vanillaColor >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }
}
