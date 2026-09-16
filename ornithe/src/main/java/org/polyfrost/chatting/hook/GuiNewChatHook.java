package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;

public interface GuiNewChatHook {
    int chatting$getRight();

    boolean chatting$isHovering();

    ChatLine chatting$getHoveredLine(int mouseY);

    String chatting$getChattingChatComponent(int mouseY, int mouseButton);

    int chatting$getTextOpacity();
}
