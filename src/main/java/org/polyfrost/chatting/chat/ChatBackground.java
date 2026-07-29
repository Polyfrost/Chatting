package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

/** Recolors the chat message background, which vanilla always fills as black at a computed alpha. */
public final class ChatBackground {

    private ChatBackground() {
    }

    /**
     * Replaces the background's black with the configured color and scales the configured alpha by the
     * vanilla one, so the text background opacity setting and message fading still apply. The default
     * opaque black leaves the vanilla color untouched.
     */
    public static int tint(int color) {
        int configured = ChattingConfig.INSTANCE.getChatBackgroundColor().getArgb();
        int alpha = (color >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }
}
