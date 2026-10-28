package org.polyfrost.chatting.chat;

//? if > 1.8.9 {
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

import java.util.List;

//? if >=26.2 {
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import org.joml.Vector4f;
//?}

//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
//?} else {
/*import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.FormattedCharSequence;
//?}

//? if >=1.21.6 {
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.polyfrost.chatting.hook.GuiRendererInterface;
//?}

//? if >=1.21.6 <26 {
/*import net.minecraft.client.gui.render.state.GuiRenderState;
*///?}

//? if =1.21.5 {
/*import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.buffers.BufferType;
import com.mojang.renderpearl.api.buffers.BufferUsage;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.function.Function;
*///?}

public final class ChatScreenshotModern {
    private ChatScreenshotModern() {
    }

    //? if >=1.21.5 {
    static void capture(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        RenderTarget rt = render(mc, lines, width, height, scale, style);
        if (rt == null) return;
        readbackAndPersist(rt);
    }
    //?}

    //? if >=1.21.6 {
    private static void chatting$flush(Minecraft mc, Object renderState, RenderTarget rt) {
        GuiRenderer guiRenderer = mc.gameRenderer.guiRenderer;
        GuiRendererInterface gri = (GuiRendererInterface) (Object) guiRenderer;
        Object saved = gri.chatting$getRenderState();
        gri.chatting$setRenderState(renderState);
        gri.chatting$render(mc.gameRenderer.fogRenderer.getBuffer(FogRenderer.FogMode.NONE), rt);
        gri.chatting$setRenderState(saved);
    }
    //?}

    //? if =1.21.5 {
    /*private static final Function<RenderTarget, RenderType> CUSTOM_TEXT_LAYER = (rt) -> RenderType.create(
            "chatting_text", 786432, false, false,
            RenderPipelines.TEXT,
            RenderType.CompositeState.builder()
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setOutputState(new RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .createCompositeState(false));

    private static final Function<RenderTarget, RenderType> CUSTOM_SOLID_LAYER = (rt) -> RenderType.create(
            "chatting_solid", 786432, false, false,
            RenderPipelines.GUI,
            RenderType.CompositeState.builder()
                    .setOutputState(new RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                    .createCompositeState(false));

    // heads blit through RenderType.guiTextured whose vertex format lacks the lightmap element the shared text layer needs so they get their own layer
    private static RenderType headLayer(ResourceLocation skin, RenderTarget rt) {
        return RenderType.create(
                "chatting_head", 786432, false, false,
                RenderPipelines.GUI_TEXTURED,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(skin, TriState.FALSE, false))
                        .setOutputState(new RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                        .createCompositeState(false));
    }

    private static class OverrideVertexProvider extends MultiBufferSource.BufferSource {
        private final RenderTarget rt;
        private final RenderType textLayer;
        private final BufferBuilder textBuffer;
        private final ByteBufferBuilder headAllocator;

        private final RenderType solidLayer;

        private RenderType headLayer;
        private BufferBuilder headBuffer;
        private BufferBuilder solidBuffer;

        private OverrideVertexProvider(ByteBufferBuilder allocator, RenderTarget rt) {
            super(allocator, Object2ObjectSortedMaps.emptyMap());
            this.rt = rt;
            this.solidLayer = CUSTOM_SOLID_LAYER.apply(rt);
            this.textLayer = CUSTOM_TEXT_LAYER.apply(rt);
            this.textBuffer = new BufferBuilder(this.sharedBuffer, textLayer.mode(), textLayer.format());
            this.headAllocator = new ByteBufferBuilder(256);
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            if (this.headBuffer != null) return this.headBuffer;
            return this.solidBuffer != null ? this.solidBuffer : this.textBuffer;
        }

        // route following blits into a dedicated head layer sharing our framebuffer because each head has its own skin texture and must flush first
        public void beginHead(ResourceLocation skin) {
            this.headLayer = headLayer(skin, rt);
            this.headBuffer = new BufferBuilder(this.headAllocator, headLayer.mode(), headLayer.format());
        }

        public void endHead() {
            if (this.headBuffer == null) return;
            this.startedBuilders.put(this.headLayer, this.headBuffer);
            this.endBatch(this.headLayer);
            this.headBuffer = null;
            this.headLayer = null;
        }

        public void beginSolid() {
            this.solidBuffer = new BufferBuilder(this.headAllocator, solidLayer.mode(), solidLayer.format());
        }

        public void endSolid() {
            if (this.solidBuffer == null) return;
            this.startedBuilders.put(this.solidLayer, this.solidBuffer);
            this.endBatch(this.solidLayer);
            this.solidBuffer = null;
        }

        public void finishDrawing() {
            this.startedBuilders.put(this.textLayer, this.textBuffer);
            this.endBatch(this.textLayer);
            this.headAllocator.close();
        }
    }

    private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        TextureTarget rt;
        try {
            rt = new TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        OverrideVertexProvider consumer = new OverrideVertexProvider(new ByteBufferBuilder(256), rt);
        GuiGraphics context = new GuiGraphics(mc, consumer);
        // this variant's vertex provider only serves the text layer so context.fill would crash on a missing vertex format and we clear the target instead
        int clearColor = style.background() ? ChatScreenshot.backgroundColor(mc) : 0x00000000;
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), clearColor);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height, 1f);
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                if (ChatHeads.INSTANCE.isLegacyShadow()) {
                    consumer.beginSolid();
                    ChatHeads.INSTANCE.drawLegacyShadow(context, info, m, y, 255);
                    consumer.endSolid();
                }
                HeadTextures.Head head = HeadTextures.INSTANCE.get(info);
                consumer.beginHead(HeadTextures.INSTANCE.texture(info, head));
                ChatHeads.INSTANCE.drawHead(context, info, head, m, y, 255);
                consumer.endHead();
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
                for (int[] o : ChatScreenshot.OUTLINE) {
                    context.drawString(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            context.drawString(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        context.flush();
        consumer.finishDrawing();
        return rt;
    }
    *///?}

    //? if >=1.21.6 <1.21.11 {
    /*private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        TextureTarget rt;
        try {
            rt = new TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        GuiRenderState renderState = new GuiRenderState();
        GuiGraphics context = new GuiGraphics(mc, renderState);
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                ChatHeads.INSTANCE.draw(context, info, m, y, 255);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
                for (int[] o : ChatScreenshot.OUTLINE) {
                    context.drawString(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            context.drawString(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }
    *///?}

    //? if >=1.21.11 <26 {
    /*private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        TextureTarget rt;
        try {
            rt = new TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        GuiRenderState renderState = new GuiRenderState();
        GuiGraphics context = new GuiGraphics(mc, renderState, 0, 0);
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                ChatHeads.INSTANCE.draw(context, info, m, y, 255);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
                for (int[] o : ChatScreenshot.OUTLINE) {
                    context.drawString(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            context.drawString(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }
    *///?}

    //? if >=26 {
    private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        TextureTarget rt;
        try {
            //? if >=26.3 {
            rt = new TextureTarget("chatting_screenshot", width * scale, height * scale, GpuFormat.RGBA8_UNORM, null);
            //?} elif >=26.2 {
            /*rt = new TextureTarget("chatting_screenshot", width * scale, height * scale, false, GpuFormat.RGBA8_UNORM);
            *///?} else {
            /*rt = new TextureTarget(null, width * scale, height * scale, false);
            *///?}
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        GuiRenderState renderState = new GuiRenderState();
        GuiGraphicsExtractor context = new GuiGraphicsExtractor(mc, renderState, 0, 0);
        //? if >=26.2 {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));
        //?} else {
        /*RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        *///?}
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                ChatHeads.INSTANCE.draw(context, info, m, y, 255);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
                for (int[] o : ChatScreenshot.OUTLINE) {
                    context.text(mc.font, bl, hx + o[0], y + o[1], 0xFF000000, false);
                }
            }
            context.text(mc.font, line.content(), hx, y, 0xFFFFFFFF, style.shadow());
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }
    //?}

    //? if =1.21.5 {
    /*private static void readbackAndPersist(RenderTarget rt) {
        int i = rt.width, j = rt.height;
        GpuTexture tex = rt.getColorTexture();
        int px = tex.getFormat().pixelSize();
        GpuDevice device = RenderSystem.getDevice();
        GpuBuffer buffer = device.createBuffer(null, BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, i * j * px);
        CommandEncoder encoder = device.createCommandEncoder();
        encoder.copyTextureToBuffer(tex, buffer, 0, () -> {
            try (GpuBuffer.ReadView view = device.createCommandEncoder().readBuffer(buffer)) {
                NativeImage image = new NativeImage(i, j, false);
                for (int k = 0; k < j; k++) {
                    for (int l = 0; l < i; l++) {
                        image.setPixelABGR(l, j - k - 1, view.data().getInt((l + k * i) * px));
                    }
                }
                ChatScreenshot.persist(image);
            } finally {
                buffer.close();
                rt.destroyBuffers();
            }
        }, 0);
    }
    *///?}

    //? if >=1.21.6 {
    private static void readbackAndPersist(RenderTarget rt) {
        int i = rt.width, j = rt.height;
        GpuTexture tex = rt.getColorTexture();
        //? if >=26.2 {
        int px = tex.getFormat().blockSize();
        //?} else {
        /*int px = tex.getFormat().pixelSize();
        *///?}
        GpuDevice device = RenderSystem.getDevice();
        GpuBuffer buffer = device.createBuffer(null, GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_READ, i * j * px);
        CommandEncoder encoder = device.createCommandEncoder();
        encoder.copyTextureToBuffer(tex, buffer, 0, () -> {
            //? if >=26.2 {
            try (GpuBufferSlice.MappedView view = buffer.map(true, false)) {
            //?} else {
            /*try (GpuBuffer.MappedView view = encoder.mapBuffer(buffer, true, false)) {
            *///?}
                NativeImage image = new NativeImage(i, j, false);
                for (int k = 0; k < j; k++) {
                    for (int l = 0; l < i; l++) {
                        image.setPixelABGR(l, j - k - 1, view.data().getInt((l + k * i) * px));
                    }
                }
                ChatScreenshot.persist(image);
            } finally {
                buffer.close();
                rt.destroyBuffers();
            }
        }, 0);
    }
    //?}
}
//?}
