package org.polyfrost.chatting.hook;

import net.minecraft.client.gui.ChatLine;

import java.awt.datatransfer.Transferable;

public interface GuiNewChatHook {
    int chatting$getRight();

    boolean chatting$isHovering();

    ChatLine chatting$getHoveredLine(int mouseY);

    Transferable chatting$getChattingChatComponent(int mouseY);

    int chatting$getTextOpacity();
}
