package org.polyfrost.chatting.chat;

import net.minecraft.client.Minecraft;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hud.ChatWindowHud;

public final class ChatBackground {

    private ChatBackground() {
    }

    // scales the configured alpha by the vanilla one so opacity settings and message fading still apply
    public static int tint(int color) {
        if (!ChattingConfig.INSTANCE.getModEnabled()) return color;
        //~ if <26.2 'gui.hud.getChat()' -> 'gui.getChat()'
        boolean focused = Chatting.INSTANCE.getPeeking() || ChatWindowHud.isEditing() || Minecraft.getInstance().gui.hud.getChat().isChatFocused();
        int configured = (focused || !ChattingConfig.INSTANCE.getSeparateUnfocusedBackground()
                ? ChattingConfig.INSTANCE.getChatBackgroundColor()
                : ChattingConfig.INSTANCE.getUnfocusedChatBackgroundColor()).getArgb();
        int alpha = (color >>> 24) * (configured >>> 24) / 255;
        return (alpha << 24) | (configured & 0xFFFFFF);
    }
}
