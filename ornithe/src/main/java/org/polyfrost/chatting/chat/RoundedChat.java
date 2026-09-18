package org.polyfrost.chatting.chat;

import net.minecraft.client.gui.Gui;
import org.polyfrost.chatting.config.ChattingConfig;

/** Draws only the outer chat corners as disjoint horizontal strips. */
public final class RoundedChat {
    private RoundedChat() {
    }

    public static void fill(int x1, int y1, int x2, int y2, int color, boolean roundTop, boolean roundBottom) {
        if (!ChattingConfig.INSTANCE.getRoundedChatCorners() || (!roundTop && !roundBottom)) {
            Gui.drawRect(x1, y1, x2, y2, color);
            return;
        }

        int width = x2 - x1;
        int height = y2 - y1;
        int radius = Math.round(ChattingConfig.INSTANCE.getChatCornerRadius());
        radius = Math.min(radius, Math.min(width / 2, roundTop && roundBottom ? height / 2 : height));
        if (radius <= 0) {
            Gui.drawRect(x1, y1, x2, y2, color);
            return;
        }

        int topRadius = roundTop ? radius : 0;
        int bottomRadius = roundBottom ? radius : 0;
        if (y1 + topRadius < y2 - bottomRadius) {
            Gui.drawRect(x1, y1 + topRadius, x2, y2 - bottomRadius, color);
        }
        if (roundTop) drawArc(x1, y1, x2, radius, true, color);
        if (roundBottom) drawArc(x1, y2 - radius, x2, radius, false, color);
    }

    private static void drawArc(int x1, int y, int x2, int radius, boolean top, int color) {
        if (radius == 0) return;
        int row = 0;
        while (row < radius) {
            int inset = inset(row, radius);
            int end = row + 1;
            while (end < radius && inset(end, radius) == inset) end++;
            int y1 = top ? y + row : y + radius - end;
            int y2 = top ? y + end : y + radius - row;
            Gui.drawRect(x1 + inset, y1, x2 - inset, y2, color);
            row = end;
        }
    }

    private static int inset(int row, int radius) {
        double dy = radius - row - 0.5;
        double dx = Math.sqrt((double) radius * radius - dy * dy);
        return Math.max(0, (int) Math.round(radius - dx));
    }
}
