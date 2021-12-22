package com.raeids.stratus.hook;

public interface GuiNewChatHook {
    int getRight();
    boolean shouldCopy();
    String getStratusChatComponent(int mouseY);
}
