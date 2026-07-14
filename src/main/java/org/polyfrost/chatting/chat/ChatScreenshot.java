package org.polyfrost.chatting.chat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType;
import org.polyfrost.oneconfig.api.notifications.v1.Notifications;
import org.polyfrost.oneconfig.utils.v1.ClipboardHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}

//? if <1.21.5 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=1.21.4 <1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
*///?}

//? if <1.21.4 {
/*import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
*///?}

public final class ChatScreenshot {

    private static final Pattern FORMATTING = Pattern.compile("§[0-9a-zA-Z]");

    private ChatScreenshot() {
    }

    public record ScreenshotStyle(boolean shadow, boolean background, boolean border) {
        public static ScreenshotStyle current() {
            ChattingConfig c = ChattingConfig.INSTANCE;
            boolean shadow = c.getTextRenderType() == 1 || c.getScreenshotForceShadow();
            boolean background = c.getScreenshotBackground();
            boolean border = c.getScreenshotBorder() && !shadow && !background;
            return new ScreenshotStyle(shadow, background, border);
        }
    }

    static final int[][] OUTLINE = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};

    // Recolor a sequence to pure black, preserving glyph shapes/positions, so the
    // outline is black regardless of the message's own color codes.
    static FormattedCharSequence blackOut(FormattedCharSequence seq) {
        return sink -> seq.accept((pos, style, cp) -> sink.accept(pos, style.withColor(0), cp));
    }

    // Vanilla chat background: black at the user's textBackgroundOpacity, full (unfaded) alpha.
    static int backgroundColor(Minecraft mc) {
        return ((int) (mc.options.textBackgroundOpacity().get() * 255.0)) << 24;
    }

    // region text copy
    public static void copyText(List<GuiMessage.Line> lines, Component fullMessage) {
        copyText(lines, fullMessage, false);
    }

    public static void copyText(List<GuiMessage.Line> lines, Component fullMessage, boolean keepFormatting) {
        if (fullMessage == null && lines.isEmpty()) {
            notifyError("Could not find chat message.");
            return;
        }

        String text;
        if (keepFormatting) {
            text = fullMessage != null ? LegacyText.INSTANCE.toFormatted(fullMessage) : collectFormatted(lines);
        } else {
            text = fullMessage != null ? fullMessage.getString() : collect(lines);
            text = FORMATTING.matcher(text).replaceAll("");
        }
        ClipboardHelper.setString(text);
        notifySuccess("Copied to clipboard", text);
    }

    private static String collectFormatted(List<GuiMessage.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (GuiMessage.Line line : lines) {
            sb.append(LegacyText.INSTANCE.toFormatted(line.content()));
        }
        return sb.toString();
    }

    private static String collect(List<GuiMessage.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (GuiMessage.Line line : lines) {
            line.content().accept((index, style, codePoint) -> {
                sb.appendCodePoint(codePoint);
                return true;
            });
        }
        return sb.toString();
    }

    static PlayerInfo headToDraw(FormattedCharSequence content) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return null;
        PlayerInfo info = ChatHeads.INSTANCE.lookup(content);
        return ChatHeads.INSTANCE.shouldDrawHead(info, ChatHeads.INSTANCE.isHidden(content)) ? info : null;
    }

    static int headOffset(FormattedCharSequence content) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return 0;
        return ChatHeads.INSTANCE.shouldOffset(ChatHeads.INSTANCE.lookup(content)) ? 10 : 0;
    }

    public static void copyImage(List<GuiMessage.Line> lines) {
        Minecraft mc = Minecraft.getInstance();
        if (lines.isEmpty()) {
            notifyError("Chat window is empty.");
            return;
        }
        int width = 0;
        for (GuiMessage.Line line : lines) {
            width = Math.max(width, headOffset(line.content()) + mc.font.width(line.content()));
        }
        if (width <= 0) {
            notifyError("Chat window is empty.");
            return;
        }
        int height = lines.size() * 9;
        ScreenshotStyle style = ScreenshotStyle.current();
        // The border draws 1px outside the glyph extent, so pad the canvas to keep it from clipping.
        int margin = style.border() ? 1 : 0;
        width += margin * 2;
        height += margin * 2;
        int scale = 2; // supersample for a crisp image, mirroring the 1.8.9 2x scale

        //? if <1.21.5 {
        /*captureLegacy(mc, lines, width, height, scale, style);
        *///?} else {
        ChatScreenshotModern.capture(mc, lines, width, height, scale, style);
        //?}
    }

    //? if <1.21.4 {
    /*private static void captureLegacy(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ScreenshotStyle style) {
        RenderTarget rt;
        try {
            rt = new TextureTarget(width * scale, height * scale, false, false);
        } catch (Exception e) {
            notifyError("Screenshot failed.");
            return;
        }
        rt.setClearColor(0f, 0f, 0f, 0f);
        rt.clear(false);
        rt.bindWrite(true);

        Matrix4f projection = new Matrix4f().setOrtho(0f, width, height, 0f, 1000f, 21000f);
        RenderSystem.setProjectionMatrix(projection, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.translation(0f, 0f, -11000f);
        RenderSystem.applyModelViewMatrix();

        GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        if (style.background()) {
            graphics.fill(0, 0, width, height, backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(graphics, info.getSkin(), m, y - 1, 8);
            }
            int hx = headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = blackOut(line.content());
                for (int[] o : OUTLINE) {
                    graphics.drawString(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            graphics.drawString(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        graphics.flush();

        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        rt.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        NativeImage image = new NativeImage(rt.width, rt.height, false);
        RenderSystem.bindTexture(rt.getColorTextureId());
        image.downloadTexture(0, false);
        flipVertically(image);
        rt.destroyBuffers();
        persist(image);
    }
    *///?}

    //? if >=1.21.4 <1.21.5 {
    /*private static net.minecraft.client.renderer.RenderStateShard.OutputStateShard chatting$fbo(RenderTarget rt) {
        return new net.minecraft.client.renderer.RenderStateShard.OutputStateShard("chatting_fbo", () -> rt.bindWrite(true), () -> {});
    }

    // The vanilla text and guiTextured layers bind the main render target when drawn, so give text and
    // player-head geometry their own layers whose output is redirected to our offscreen framebuffer.
    private static final java.util.function.Function<RenderTarget, RenderType> CUSTOM_TEXT_LAYER = (rt) -> RenderType.create(
            "chatting_text", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, 786432, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_TEXT_SHADER)
                    .setTextureState(net.minecraft.client.renderer.RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(net.minecraft.client.renderer.RenderStateShard.LIGHTMAP)
                    .setOutputState(chatting$fbo(rt))
                    .createCompositeState(false));

    private static RenderType headLayer(net.minecraft.resources.ResourceLocation skin, RenderTarget rt) {
        return RenderType.create(
                "chatting_head", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR,
                com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, 786432,
                RenderType.CompositeState.builder()
                        .setTextureState(new net.minecraft.client.renderer.RenderStateShard.TextureStateShard(skin, net.minecraft.util.TriState.FALSE, false))
                        .setShaderState(net.minecraft.client.renderer.RenderStateShard.POSITION_TEXTURE_COLOR_SHADER)
                        .setTransparencyState(net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(net.minecraft.client.renderer.RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setOutputState(chatting$fbo(rt))
                        .createCompositeState(false));
    }

    private static class OverrideVertexProvider extends net.minecraft.client.renderer.MultiBufferSource.BufferSource {
        private final RenderTarget rt;
        private final RenderType textLayer;
        private final com.mojang.blaze3d.vertex.BufferBuilder textBuffer;
        private final com.mojang.blaze3d.vertex.ByteBufferBuilder headAllocator;

        private RenderType headLayer;
        private com.mojang.blaze3d.vertex.BufferBuilder headBuffer;

        private OverrideVertexProvider(com.mojang.blaze3d.vertex.ByteBufferBuilder allocator, RenderTarget rt) {
            super(allocator, it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps.emptyMap());
            this.rt = rt;
            this.textLayer = CUSTOM_TEXT_LAYER.apply(rt);
            this.textBuffer = new com.mojang.blaze3d.vertex.BufferBuilder(this.sharedBuffer, textLayer.mode(), textLayer.format());
            this.headAllocator = new com.mojang.blaze3d.vertex.ByteBufferBuilder(256);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer getBuffer(RenderType renderType) {
            return this.headBuffer != null ? this.headBuffer : this.textBuffer;
        }

        // Route the following blit(s) into a dedicated head layer sharing our framebuffer. Each head
        // has its own skin texture, so its batch must be flushed before the next head begins.
        public void beginHead(net.minecraft.resources.ResourceLocation skin) {
            this.headLayer = headLayer(skin, rt);
            this.headBuffer = new com.mojang.blaze3d.vertex.BufferBuilder(this.headAllocator, headLayer.mode(), headLayer.format());
        }

        public void endHead() {
            if (this.headBuffer == null) return;
            this.startedBuilders.put(this.headLayer, this.headBuffer);
            this.endBatch(this.headLayer);
            this.headBuffer = null;
            this.headLayer = null;
        }

        public void finishDrawing() {
            this.startedBuilders.put(this.textLayer, this.textBuffer);
            this.endBatch(this.textLayer);
            this.headAllocator.close();
        }
    }

    private static void captureLegacy(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ScreenshotStyle style) {
        RenderTarget rt;
        try {
            rt = new TextureTarget(width * scale, height * scale, false);
        } catch (Exception e) {
            notifyError("Chat window is empty.");
            return;
        }
        int bg = style.background() ? backgroundColor(mc) : 0x00000000;
        rt.setClearColor(((bg >> 16) & 0xFF) / 255f, ((bg >> 8) & 0xFF) / 255f, (bg & 0xFF) / 255f, (bg >>> 24) / 255f);
        rt.clear();
        OverrideVertexProvider consumer = new OverrideVertexProvider(new com.mojang.blaze3d.vertex.ByteBufferBuilder(256), rt);
        GuiGraphics context = new GuiGraphics(mc, consumer);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height, 1f);
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = headToDraw(line.content());
            if (info != null) {
                consumer.beginHead(info.getSkin().texture());
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, y - 1, 8);
                consumer.endHead();
            }
            int hx = headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = blackOut(line.content());
                for (int[] o : OUTLINE) {
                    context.drawString(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            context.drawString(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        context.flush();
        consumer.finishDrawing();
        mc.getMainRenderTarget().bindWrite(true);

        NativeImage image = new NativeImage(rt.width, rt.height, false);
        RenderSystem.bindTexture(rt.getColorTextureId());
        image.downloadTexture(0, false);
        flipVertically(image);
        rt.destroyBuffers();
        persist(image);
    }
    *///?}

    //? if <1.21.5 {
    /*private static void flipVertically(NativeImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h / 2; y++) {
            for (int x = 0; x < w; x++) {
                //? if <1.21.4 {
                /^int top = image.getPixelRGBA(x, y);
                int bottom = image.getPixelRGBA(x, h - 1 - y);
                image.setPixelRGBA(x, y, bottom);
                image.setPixelRGBA(x, h - 1 - y, top);
                ^///?} else {
                int top = image.getPixel(x, y);
                int bottom = image.getPixel(x, h - 1 - y);
                image.setPixel(x, y, bottom);
                image.setPixel(x, h - 1 - y, top);
                //?}
            }
        }
    }
    *///?}

    static void persist(NativeImage image) {
        int copyMode = ChattingConfig.INSTANCE.getCopyMode();
        boolean save = copyMode != 1;
        boolean clip = copyMode != 0;
        if (!save && !clip) {
            throw new IllegalStateException("Attempted to save a screenshot with no destination");
        }
        try {
            if (save) {
                File dir = new File("screenshots/chat");
                dir.mkdirs();
                File file = uniqueFile(dir);
                image.writeToFile(file);
            }
            if (clip) {
                ClipboardHelper.setImage(toBufferedImage(image));
            }
            if (save && clip) {
                notifySuccess("Chatting", "Screenshot saved to clipboard and file.");
            } else if (save) {
                notifySuccess("Chatting", "Screenshot saved to file.");
            } else if (clip) {
                notifySuccess("Chatting", "Screenshot saved to clipboard.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyError("Screenshot failed.");
        } finally {
            image.close();
        }
    }

    private static BufferedImage toBufferedImage(NativeImage image) throws Exception {
        File temp = File.createTempFile("chatting-clipboard", ".png");
        try {
            image.writeToFile(temp);
            return ImageIO.read(temp);
        } finally {
            temp.delete();
        }
    }

    private static File uniqueFile(File dir) {
        String stamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
        int i = 1;
        File file;
        while ((file = new File(dir, stamp + (i == 1 ? "" : "_" + i) + ".png")).exists()) {
            i++;
        }
        return file;
    }

    static void notifySuccess(String title, String message) {
        Notifications.send(title, message, NotificationType.SUCCESS);
    }

    static void notifyError(String message) {
        Notifications.send("Chatting", message, NotificationType.ERROR);
    }
}
