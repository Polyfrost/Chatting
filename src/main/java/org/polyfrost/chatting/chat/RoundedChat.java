package org.polyfrost.chatting.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
//? if >=1.21.6 {
import org.joml.Matrix3x2fStack;
//?} else
//import com.mojang.blaze3d.vertex.PoseStack;
import org.polyfrost.chatting.config.ChattingConfig;

// rounded corners are approximated as horizontal strips under a downscaled pose so the arc rasterizes at screen resolution
public final class RoundedChat {

    // most strip rows per unit, enough for scale products up to 8
    private static final int MAX_SUBPIXELS = 8;

    // corner insets per subpixel radius, filled lazily and only touched on the render thread
    private static final int[][] INSETS = new int[16 * MAX_SUBPIXELS + 1][];

    @FunctionalInterface
    public interface FillSink {
        void fill(int x1, int y1, int x2, int y2, int color);
    }

    public interface PoseScaler {
        // runs body with the pose scaled by factor so sink coordinates inside body are in 1/factor units
        void scaled(float factor, Runnable body);

        // how many gui pixels one sink unit currently covers vertically
        float scale();
    }

    private RoundedChat() {
    }

    public static boolean enabled() {
        return ChattingConfig.INSTANCE.getRoundedChatCorners();
    }

    // mirrors ChatComponent's chatBottom
    public static int chatBottom(int guiHeight) {
        float scale = (float) (double) Minecraft.getInstance().options.chatScale().get();
        return Mth.floor((guiHeight - 40) / scale);
    }

    //? if >=1.21.6 {
    public static PoseScaler scaler(Matrix3x2fStack pose) {
        return new PoseScaler() {
            @Override
            public void scaled(float factor, Runnable body) {
                pose.pushMatrix();
                pose.scale(factor, factor);
                body.run();
                pose.popMatrix();
            }

            @Override
            public float scale() {
                return Math.abs(pose.m11());
            }
        };
    }
    //?} elif > 1.8.9 {
    /*public static PoseScaler scaler(PoseStack pose) {
        return new PoseScaler() {
            @Override
            public void scaled(float factor, Runnable body) {
                pose.pushPose();
                pose.scale(factor, factor, 1f);
                body.run();
                pose.popPose();
            }

            @Override
            public float scale() {
                return Math.abs(pose.last().pose().m11());
            }
        };
    }
    *///?} else {
    /*// the legacy pose wraps the GL matrix stack so the caller states the scale it has applied
    public static PoseScaler scaler(PoseStack pose, float scale) {
        return new PoseScaler() {
            @Override
            public void scaled(float factor, Runnable body) {
                pose.pushPose();
                pose.scale(factor, factor, 1f);
                body.run();
                pose.popPose();
            }

            @Override
            public float scale() {
                return scale;
            }
        };
    }
    *///?}

    // strip rows per sink unit, one per screen pixel so no strip is thinner than what can be drawn
    private static int subpixels(PoseScaler poser) {
        float pixels = (float) Minecraft.getInstance().getWindow().getGuiScale() * poser.scale();
        return Math.max(1, Math.min(MAX_SUBPIXELS, (int) Math.ceil(pixels - 0.001f)));
    }

    // emitted strips are disjoint so translucent colors blend exactly once per pixel
    public static void fill(FillSink sink, PoseScaler poser, int x1, int y1, int x2, int y2, int color,
                            boolean roundTop, boolean roundBottom) {
        int radius = enabled() ? Math.round(ChattingConfig.INSTANCE.getChatCornerRadius()) : 0;
        fill(sink, poser, x1, y1, x2, y2, color, radius, roundTop, roundBottom);
    }

    // a radius of 0 or less draws a plain rectangle
    public static void fill(FillSink sink, PoseScaler poser, int x1, int y1, int x2, int y2, int color,
                            int cornerRadius, boolean roundTop, boolean roundBottom) {
        int w = x2 - x1, h = y2 - y1;
        if (cornerRadius <= 0 || (!roundTop && !roundBottom) || w <= 0 || h <= 0) {
            sink.fill(x1, y1, x2, y2, color);
            return;
        }
        int r = Math.min(cornerRadius, Math.min(w / 2, (roundTop && roundBottom) ? h / 2 : h));
        if (r <= 0) {
            sink.fill(x1, y1, x2, y2, color);
            return;
        }
        int radius = r;
        int sub = subpixels(poser);
        if (sub == 1) {
            emitStrips(sink, x1, y1, x2, y2, color, roundTop, roundBottom, radius, 1);
            return;
        }
        poser.scaled(1f / sub, () -> emitStrips(sink, x1, y1, x2, y2, color, roundTop, roundBottom, radius, sub));
    }

    private static void emitStrips(FillSink sink, int x1, int y1, int x2, int y2, int color,
                                   boolean roundTop, boolean roundBottom, int radius, int sub) {
        int sx1 = x1 * sub, sy1 = y1 * sub, sx2 = x2 * sub, sy2 = y2 * sub;
        int r = radius * sub;
        int[] insets = insets(r);
        int topR = roundTop ? r : 0, botR = roundBottom ? r : 0;
        if (sy1 + topR < sy2 - botR) sink.fill(sx1, sy1 + topR, sx2, sy2 - botR, color);
        for (int i = 0; i < topR; ) {
            int inset = insets[i];
            int j = i + 1;
            while (j < topR && insets[j] == inset) j++;
            sink.fill(sx1 + inset, sy1 + i, sx2 - inset, sy1 + j, color);
            i = j;
        }
        for (int i = 0; i < botR; ) {
            int inset = insets[i];
            int j = i + 1;
            while (j < botR && insets[j] == inset) j++;
            sink.fill(sx1 + inset, sy2 - j, sx2 - inset, sy2 - i, color);
            i = j;
        }
    }

    // circle equation x inset for each row of a radius r corner sampled at row center with row 0 outermost
    private static int[] insets(int r) {
        int[] cached = r < INSETS.length ? INSETS[r] : null;
        if (cached != null) return cached;
        int[] insets = new int[r];
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            double dx = Math.sqrt((double) r * r - dy * dy);
            insets[i] = Math.max(0, (int) Math.round(r - dx));
        }
        if (r < INSETS.length) INSETS[r] = insets;
        return insets;
    }
}
