package org.polyfrost.chatting.chat;

import net.minecraft.client.gui.ChatLine;

/** Frame-local target selected by the copy affordance. */
public final class ChatCopyButton {
    private static ChatLine hoveredLine;

    private ChatCopyButton() {
    }

    public static void reset() {
        hoveredLine = null;
    }

    public static void hover(ChatLine line) {
        hoveredLine = line;
    }

    public static ChatLine consume() {
        ChatLine line = hoveredLine;
        hoveredLine = null;
        return line;
    }
}
