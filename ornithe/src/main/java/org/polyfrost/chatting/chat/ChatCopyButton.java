package org.polyfrost.chatting.chat;

/** Frame-local state shared by the chat renderer and GuiChat click handler. */
public final class ChatCopyButton {
    private static String hoveredText;

    private ChatCopyButton() {
    }

    public static void reset() {
        hoveredText = null;
    }

    public static void hover(String text) {
        hoveredText = text;
    }

    public static String consumeHoveredText() {
        String text = hoveredText;
        hoveredText = null;
        return text;
    }
}
