package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

public final class ChatButtons {

    public static final int BUTTON_WIDTH = 9;

    /** Horizontal gap, in chat-local pixels, between adjacent per-line buttons. */
    public static final int BUTTON_GAP = 1;

    /**
     * Padding, in chat-local pixels, that the vanilla message background extends past the message
     * text on the right side (the {@code + 4 + 4} in {@code ChatComponent}'s background fill).
     */
    public static final int BACKGROUND_RIGHT_PADDING = 8;

    /**
     * The {@code pose.translate(4, 0)} that {@code ChatComponent#render} applies (inside the chat
     * scale) before drawing messages. The per-line buttons are drawn without that translate, so it
     * has to be added to line them up with the background's right edge.
     */
    public static final int TEXT_LEFT_OFFSET = 4;

    /**
     * Offset, in chat-local pixels, from the message text width to the right edge of the message
     * background. The per-line buttons start here so they sit just outside the background when it is
     * not being extended.
     */
    public static final int BACKGROUND_RIGHT_EDGE = TEXT_LEFT_OFFSET + BACKGROUND_RIGHT_PADDING;

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

    /** Total width, in chat-local pixels, occupied by the per-line button strip. */
    public static int perLineButtonsWidth() {
        int count = perLineButtonCount();
        if (count == 0) return 0;
        return count * BUTTON_WIDTH + (count - 1) * BUTTON_GAP;
    }

    public static int extraBackgroundWidth() {
        if (!ChattingConfig.INSTANCE.getExtendBG()) return 0;
        return perLineButtonsWidth();
    }
}
