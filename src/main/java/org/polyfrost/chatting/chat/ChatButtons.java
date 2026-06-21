package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

public final class ChatButtons {

    public static final int BUTTON_WIDTH = 9;

    private ChatButtons() {
    }

    public static int perLineButtonCount() {
        int count = 0;
        if (ChattingConfig.INSTANCE.getChatCopy()) count++;
        if (ChattingConfig.INSTANCE.getChatDelete()) count++;
        return count;
    }

    public static boolean hasPerLineButtons() {
        return perLineButtonCount() > 0;
    }

    public static int extraBackgroundWidth() {
        if (!ChattingConfig.INSTANCE.getExtendBG()) return 0;
        return perLineButtonCount() * BUTTON_WIDTH;
    }
}
