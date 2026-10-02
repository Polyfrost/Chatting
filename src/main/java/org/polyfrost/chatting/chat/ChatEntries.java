package org.polyfrost.chatting.chat;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.hook.ChatEntryHook;

import java.util.List;

/^*
 * Groups the visual lines vanilla produced by wrapping one chat message.
 * drawnChatLines is ordered bottom-up and a message's lines are inserted
 * consecutively, so an entry always occupies a contiguous index range.
 ^/
public final class ChatEntries {
    private ChatEntries() {
    }

    public static IChatComponent parent(ChatLine line) {
        return line instanceof ChatEntryHook ? ((ChatEntryHook) line).chatting$getParent() : null;
    }

    /^* Compares by reference so two separate messages with equal text stay distinct. ^/
    public static boolean sameEntry(ChatLine a, ChatLine b) {
        if (a == null || b == null) return false;
        if (a == b) return true;
        IChatComponent parent = parent(a);
        return parent != null && parent == parent(b);
    }

    /^* Lowest index of the run holding {@code index}, i.e. the message's last visual line. ^/
    public static int entryStart(List<ChatLine> lines, int index) {
        ChatLine line = lines.get(index);
        int start = index;
        while (start > 0 && sameEntry(lines.get(start - 1), line)) start--;
        return start;
    }

    /^* Highest index of the run holding {@code index}, i.e. the message's first visual line. ^/
    public static int entryEnd(List<ChatLine> lines, int index) {
        ChatLine line = lines.get(index);
        int end = index;
        while (end + 1 < lines.size() && sameEntry(lines.get(end + 1), line)) end++;
        return end;
    }
}
*///?}
