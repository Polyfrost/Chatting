package com.raeids.stratus.hook;

public interface GuiNewChatHook {
    int getX();
    int getY();
    boolean shouldCopy();
    String copyString();
}
