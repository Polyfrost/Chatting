package org.polyfrost.chatting.chat;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.ChatLine;

/^* Frame-local target selected by the delete affordance. ^/
public final class ChatDeleteButton {
    private static ChatLine hoveredLine;

    private ChatDeleteButton() {
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
*///?}
