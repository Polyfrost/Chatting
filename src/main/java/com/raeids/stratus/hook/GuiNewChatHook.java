package com.raeids.stratus.hook;

import net.minecraft.client.gui.ChatLine;

public interface GuiNewChatHook {
    int getRight();
    boolean shouldCopy();
    String getStratusChatComponent(int mouseY);
    default ChatLine getFullMessage(ChatLine line) {
        throw new AssertionError("getFullMessage not overridden on GuiNewChat");
    }
    String getPrevText();
    void setPrevText(String prevText);
}
