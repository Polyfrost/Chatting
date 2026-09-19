package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

/** Shared geometry for the 1.8.9 chat-button renderer adapters. */
public final class ChatButtons {
    public static final int BUTTON_SIZE = 9;
    public static final int BUTTON_GAP = 1;

    private ChatButtons() {
    }

    public static int perLineButtonCount() {
        int count = 0;
        if (ChattingConfig.INSTANCE.getChatCopy()) count++;
        if (ChattingConfig.INSTANCE.getChatDelete()) count++;
        return count;
    }

    /** Width added to a chat-line background while the chat screen is open. */
    public static int extraBackgroundWidth() {
        if (!ChattingConfig.INSTANCE.getExtendBG()) return 0;
        int count = perLineButtonCount();
        return count == 0 ? 0 : count * BUTTON_SIZE + (count - 1) * BUTTON_GAP + 1;
    }
}
