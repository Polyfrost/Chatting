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
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
*///?} else {
import net.minecraft.client.GuiMessage;
//?}

//? if <1.21.5 {
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
//?}

public final class ChatScreenshot {

    private static final Pattern FORMATTING = Pattern.compile("§[0-9a-zA-Z]");

    private ChatScreenshot() {
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
        boolean shadow = ChattingConfig.INSTANCE.getTextRenderType() == 1;
        int scale = 2; // supersample for a crisp image, mirroring the 1.8.9 2x scale

        //? if <1.21.4 {
        captureLegacy(mc, lines, width, height, scale, shadow);
        //?} elif <1.21.5 {
        /*notify("Image screenshot isn't supported on 1.21.4 yet — use right-click / the copy button to copy the text.");
        *///?} else {
        /*ChatScreenshotModern.capture(mc, lines, width, height, scale, shadow);
        *///?}
    }

    //? if <1.21.5 {
    private static void captureLegacy(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        RenderTarget rt;
        try {
            //? if <1.21.4 {
            rt = new TextureTarget(width * scale, height * scale, false, false);
            //?} else {
            /*rt = new TextureTarget(width * scale, height * scale, false);
            *///?}
        } catch (Exception e) {
            notifyError("Screenshot failed.");
            return;
        }
        rt.setClearColor(0f, 0f, 0f, 0f);
        //? if <1.21.4 {
        rt.clear(false);
        //?} else {
        /*rt.clear();
        *///?}
        rt.bindWrite(true);

        //? if <1.21.4 {
        Matrix4f projection = new Matrix4f().setOrtho(0f, width, height, 0f, 1000f, 21000f);
        RenderSystem.setProjectionMatrix(projection, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.translation(0f, 0f, -11000f);
        RenderSystem.applyModelViewMatrix();
        //?} else {
        /*Matrix4f projection = new Matrix4f().setOrtho(0f, width, height, 0f, -21000f, 21000f);
        RenderSystem.setProjectionMatrix(projection, com.mojang.blaze3d.ProjectionType.ORTHOGRAPHIC);
        *///?}

        GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        int y = 0;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(graphics, info.getSkin(), 0, y - 1, 8);
            }
            graphics.drawString(mc.font, line.content(), headOffset(line.content()), y, 0xFFFFFFFF, shadow);
            y += 9;
        }
        graphics.flush();

        //? if <1.21.4 {
        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        //?}
        rt.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        NativeImage image = new NativeImage(rt.width, rt.height, false);
        RenderSystem.bindTexture(rt.getColorTextureId());
        image.downloadTexture(0, false);
        flipVertically(image);
        rt.destroyBuffers();
        persist(image);
    }

    private static void flipVertically(NativeImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h / 2; y++) {
            for (int x = 0; x < w; x++) {
                //? if <1.21.4 {
                int top = image.getPixelRGBA(x, y);
                int bottom = image.getPixelRGBA(x, h - 1 - y);
                image.setPixelRGBA(x, y, bottom);
                image.setPixelRGBA(x, h - 1 - y, top);
                //?} else {
                /*int top = image.getPixel(x, y);
                int bottom = image.getPixel(x, h - 1 - y);
                image.setPixel(x, y, bottom);
                image.setPixel(x, h - 1 - y, top);
                *///?}
            }
        }
    }
    //?}

    static void persist(NativeImage image) {
        int copyMode = ChattingConfig.INSTANCE.getCopyMode();
        boolean save = copyMode != 1;
        boolean clip = copyMode != 0;
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
            notifySuccess("Chatting", "Chat screenshotted successfully.");
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

    static void notify(String message) {
        Notifications.send("Chatting", message, NotificationType.INFO);
    }

    static void notifySuccess(String title, String message) {
        Notifications.send(title, message, NotificationType.SUCCESS);
    }

    static void notifyError(String message) {
        Notifications.send("Chatting", message, NotificationType.ERROR);
    }
}
