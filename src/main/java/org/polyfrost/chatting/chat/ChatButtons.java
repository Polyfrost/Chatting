package org.polyfrost.chatting.chat;

import org.polyfrost.chatting.config.ChattingConfig;

public final class ChatButtons {

    public static final int BUTTON_WIDTH = 9;

    public static final int BUTTON_GAP = 1;

    // the + 4 + 4 in ChatComponent's background fill
    public static final int BACKGROUND_RIGHT_PADDING = 8;

    // the pose.translate(4, 0) in ChatComponent#render which the buttons are drawn without
    public static final int TEXT_LEFT_OFFSET = 4;

    public static final int BACKGROUND_RIGHT_EDGE = TEXT_LEFT_OFFSET + BACKGROUND_RIGHT_PADDING;

    private ChatButtons() {
    }

    public static int perLineButtonCount() {
        int count = 0;
        if (ChattingConfig.INSTANCE.getChatCopy()) count++;
        if (ChattingConfig.INSTANCE.getChatDelete()) count++;
        return count;
    }

    public static int globalButtonCount() {
        int count = 0;
        if (ChattingConfig.INSTANCE.getChatScreenshot()) count++;
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) count++;
        if (ChattingConfig.INSTANCE.getChatSearch()) count++;
        return count;
    }

    public static boolean hasPerLineButtons() {
        return perLineButtonCount() > 0;
    }

    public static boolean hasGlobalButtons() {
        return globalButtonCount() > 0;
    }

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
