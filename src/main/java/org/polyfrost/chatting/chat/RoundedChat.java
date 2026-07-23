package org.polyfrost.chatting.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.polyfrost.chatting.config.ChattingConfig;

/**
 * Draws chat background rectangles with optionally rounded outer corners, approximated
 * as horizontal strips so it works with any plain fill(x1, y1, x2, y2, color) sink.
 * Strips are emitted under a temporarily downscaled pose at {@link #SUBPIXELS} rows per
 * chat pixel, so the arc is rasterized at (or beyond) screen resolution.
 */
public final class RoundedChat {

    /** Strip rows per chat pixel; covers GUI scale times chat/HUD scale products up to 8. */
    private static final int SUBPIXELS = 8;

    @FunctionalInterface
    public interface FillSink {
        void fill(int x1, int y1, int x2, int y2, int color);
    }

    /** Runs body with the pose scaled by factor, so sink coordinates inside body are in 1/factor units. */
    @FunctionalInterface
    public interface PoseScaler {
        void scaled(float factor, Runnable body);
    }

    private RoundedChat() {
    }

    public static boolean enabled() {
        return ChattingConfig.INSTANCE.getRoundedChatCorners();
    }

    /** Mirrors ChatComponent's chatBottom: Mth.floor((screenHeight - 40) / chatScale). */
    public static int chatBottom(int guiHeight) {
        float scale = (float) (double) Minecraft.getInstance().options.chatScale().get();
        return Mth.floor((guiHeight - 40) / scale);
    }

    /**
     * Draws the rect [x1,y1)-[x2,y2) with the requested corners rounded. All emitted strips
     * are disjoint, so translucent colors blend exactly once per pixel.
     */
    public static void fill(FillSink sink, PoseScaler poser, int x1, int y1, int x2, int y2, int color,
                            boolean roundTop, boolean roundBottom) {
        int w = x2 - x1, h = y2 - y1;
        if (!enabled() || (!roundTop && !roundBottom) || w <= 0 || h <= 0) {
            sink.fill(x1, y1, x2, y2, color);
            return;
        }
        int r = Math.round(ChattingConfig.INSTANCE.getChatCornerRadius());
        r = Math.min(r, Math.min(w / 2, (roundTop && roundBottom) ? h / 2 : h));
        if (r <= 0) {
            sink.fill(x1, y1, x2, y2, color);
            return;
        }
        int radius = r;
        poser.scaled(1f / SUBPIXELS, () -> emitStrips(sink, x1, y1, x2, y2, color, roundTop, roundBottom, radius));
    }

    private static void emitStrips(FillSink sink, int x1, int y1, int x2, int y2, int color,
                                   boolean roundTop, boolean roundBottom, int radius) {
        int sx1 = x1 * SUBPIXELS, sy1 = y1 * SUBPIXELS, sx2 = x2 * SUBPIXELS, sy2 = y2 * SUBPIXELS;
        int r = radius * SUBPIXELS;
        int topR = roundTop ? r : 0, botR = roundBottom ? r : 0;
        if (sy1 + topR < sy2 - botR) sink.fill(sx1, sy1 + topR, sx2, sy2 - botR, color);
        for (int i = 0; i < topR; ) {
            int inset = inset(i, r);
            int j = i + 1;
            while (j < topR && inset(j, r) == inset) j++;
            sink.fill(sx1 + inset, sy1 + i, sx2 - inset, sy1 + j, color);
            i = j;
        }
        for (int i = 0; i < botR; ) {
            int inset = inset(i, r);
            int j = i + 1;
            while (j < botR && inset(j, r) == inset) j++;
            sink.fill(sx1 + inset, sy2 - j, sx2 - inset, sy2 - i, color);
            i = j;
        }
    }

    /** Circle-equation x-inset for row i (sampled at row center) of a radius-r corner; row 0 is outermost. */
    private static int inset(int i, int r) {
        double dy = r - i - 0.5;
        double dx = Math.sqrt((double) r * r - dy * dy);
        return Math.max(0, (int) Math.round(r - dx));
    }
}
