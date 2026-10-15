package org.polyfrost.chatting.chat;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hud.ChatWindowHud;
import org.polyfrost.chatting.mixin.ChatComponentAccessor;
import org.polyfrost.oneconfig.api.platform.v1.DesktopHelper;
import org.polyfrost.oneconfig.api.platform.v1.Keys;
import org.polyfrost.oneconfig.api.platform.v1.Platform;

import java.util.List;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} elif > 1.8.9 {
/*import net.minecraft.client.GuiMessage;
*///?} else {
/*import net.minecraft.client.gui.ChatMessage;
import org.polyfrost.chatting.hook.ChatLineHook;
*///?}

public final class ChatHover {

    private ChatHover() {
    }

    public static int hoveredLine(ChatComponent chat, double mouseX, double mouseY) {
        return withinStrip(chat, mouseX) ? hoveredLine(chat, mouseY) : -1;
    }

    // spans the chat background and the per line button strip so hovering copy or delete keeps the entry hovered
    private static boolean withinStrip(ChatComponent chat, double mouseX) {
        double scale = ((ChatComponentAccessor) chat).chatting$getScale();
        return mouseX >= 0 && mouseX < (stripStart(chat) + ChatButtons.perLineButtonsWidth()) * scale;
    }

    public static int stripStart(ChatComponent chat) {
        ChatComponentAccessor acc = (ChatComponentAccessor) chat;
        //? if > 1.8.9 {
        return Mth.ceil(acc.chatting$getWidth() / (float) acc.chatting$getScale()) + ChatButtons.BACKGROUND_RIGHT_EDGE;
        //?} else {
        /*return Mth.ceil(acc.chatting$getWidth() / acc.chatting$getScale()) + 5 + Math.round(2 / acc.chatting$getScale());
        *///?}
    }

    public static boolean highlighted(ChatComponent chat, double mouseX, double mouseY, int line) {
        int hovered = hoveredLine(chat, mouseX, mouseY);
        if (hovered < 0) return false;
        // ctrl click on copy or delete acts on a single line
        if (mouseX >= stripStart(chat) * ((ChatComponentAccessor) chat).chatting$getScale() && ctrlHeld()) return line == hovered;
        return line >= entryBottom(chat, hovered) && line <= entryTop(chat, hovered);
    }

    public static boolean ctrlHeld() {
        Keys keys = Platform.compatibility().keys();
        return DesktopHelper.isMac()
                ? keyDown(keys.getKeyLeftSuper()) || keyDown(keys.getKeyRightSuper())
                : keyDown(keys.getKeyLeftControl()) || keyDown(keys.getKeyRightControl());
    }

    private static int hoveredLine(ChatComponent chat, double mouseY) {
        if (!ChattingConfig.INSTANCE.getModEnabled() || !chat.isChatFocused()) return -1;
        ChatComponentAccessor acc = (ChatComponentAccessor) chat;
        if (acc.chatting$getScale() <= 0.0) return -1;
        int row = row(acc, mouseY - slideY(chat));
        if (row < 0 || row >= visibleRows(chat)) return -1;
        return row + scrollPos();
    }

    public static int entryBottom(ChatComponent chat, int line) {
        //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
        List<GuiMessage.Line> lines = ((ChatComponentAccessor) chat).chatting$getTrimmedMessages();
        //~ if =1.8.9 'lines.get(line).endOfEntry()' -> '((ChatLineHook) lines.get(line)).chatting$isEndOfEntry()'
        while (line > 0 && !lines.get(line).endOfEntry()) line--;
        return line;
    }

    public static int entryTop(ChatComponent chat, int line) {
        //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
        List<GuiMessage.Line> lines = ((ChatComponentAccessor) chat).chatting$getTrimmedMessages();
        //~ if =1.8.9 'lines.get(line + 1).endOfEntry()' -> '((ChatLineHook) lines.get(line + 1)).chatting$isEndOfEntry()'
        while (line + 1 < lines.size() && !lines.get(line + 1).endOfEntry()) line++;
        return line;
    }

    // the drawn scroll position, which lags chatScrollbarPos while smooth scrolling animates
    public static int scrollPos() {
        return ChatScrolling.INSTANCE.pos();
    }

    public static float slideY(ChatComponent chat) {
        //~ if =1.8.9 'chatting$getScrollbarPos() > 0' -> 'chatting$hasNewMessagesSinceScroll()'
        float dy = SmoothChat.INSTANCE.translateY(((ChatComponentAccessor) chat).chatting$getScrollbarPos() > 0);
        return ChatWindowHud.isActive() ? dy / ChatWindowHud.chatScale() : dy;
    }

    public static int visibleRows(ChatComponent chat) {
        return Math.min(chat.getLinesPerPage(), ((ChatComponentAccessor) chat).chatting$getTrimmedMessages().size() - scrollPos());
    }

    private static boolean keyDown(int key) {
        //? if >=26.3 || =1.8.9 {
        return InputConstants.isKeyDown(key);
        //?} elif >=1.21.10 {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
        *///?} else {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
        *///?}
    }

    private static int row(ChatComponentAccessor acc, double mouseY) {
        double d = Minecraft.getInstance().getWindow().getGuiScaledHeight() - mouseY - 40.0;
        // ceil minus 1 because line backgrounds span y1 <= y < y2
        //~ if =1.8.9 'acc.chatting$getLineHeight()' -> '9'
        return (int) Math.ceil(d / (acc.chatting$getScale() * acc.chatting$getLineHeight())) - 1;
    }
}
