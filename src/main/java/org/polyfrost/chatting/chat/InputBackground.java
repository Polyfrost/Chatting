package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

public final class InputBackground {

    private InputBackground() {
    }

    // scales the configured alpha by the vanilla one so the text background opacity setting still applies
    public static int tint(int color) {
        if (!ChattingConfig.INSTANCE.getModEnabled()) return color;
        int configured = ChattingConfig.INSTANCE.getInputBackgroundColor().getArgb();
        int alpha = (color >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }

    // 0 when the corners are square
    public static int radius() {
        return ChattingConfig.INSTANCE.getRoundedInputCorners()
                ? Math.round(ChattingConfig.INSTANCE.getInputCornerRadius())
                : 0;
    }
}
