package com.raeids.stratus.hook;

import net.minecraft.client.gui.ChatLine;

import java.awt.datatransfer.Transferable;

public interface GuiNewChatHook {
    int getRight();

    boolean shouldCopy();

    Transferable getStratusChatComponent(int mouseY);

    default ChatLine getFullMessage(ChatLine line) {
        throw new AssertionError("getFullMessage not overridden on GuiNewChat");
    }

    String getPrevText();

    void setPrevText(String prevText);
}
