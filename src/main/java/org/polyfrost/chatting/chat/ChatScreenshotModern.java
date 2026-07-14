package org.polyfrost.chatting.chat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

import java.util.List;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}

//? if >=1.21.5 <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
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
        net.minecraft.client.gui.render.GuiRenderer guiRenderer = mc.gameRenderer.guiRenderer;
        org.polyfrost.chatting.hook.GuiRendererInterface gri = (org.polyfrost.chatting.hook.GuiRendererInterface) (Object) guiRenderer;
        Object saved = gri.chatting$getRenderState();
        gri.chatting$setRenderState(renderState);
        gri.chatting$render(mc.gameRenderer.fogRenderer.getBuffer(net.minecraft.client.renderer.fog.FogRenderer.FogMode.NONE), rt);
        gri.chatting$setRenderState(saved);
    }
    //?}

    //? if >=1.21.5 <1.21.6 {
    /*private static final java.util.function.Function<RenderTarget, RenderType> CUSTOM_TEXT_LAYER = (rt) -> RenderType.create(
            "chatting_text", 786432, false, false,
            net.minecraft.client.renderer.RenderPipelines.TEXT,
            RenderType.CompositeState.builder()
                    .setTextureState(net.minecraft.client.renderer.RenderStateShard.NO_TEXTURE)
                    .setOutputState(new net.minecraft.client.renderer.RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                    .setLightmapState(net.minecraft.client.renderer.RenderStateShard.LIGHTMAP)
                    .createCompositeState(false));

    // A textured GUI layer bound to the given skin and redirected to our framebuffer. Player heads
    // blit through RenderType.guiTextured, whose vertex format lacks the lightmap element the shared
    // text layer requires, so they need their own compatible layer instead of the text buffer.
    private static RenderType headLayer(net.minecraft.resources.ResourceLocation skin, RenderTarget rt) {
        return RenderType.create(
                "chatting_head", 786432, false, false,
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                RenderType.CompositeState.builder()
                        .setTextureState(new net.minecraft.client.renderer.RenderStateShard.TextureStateShard(skin, net.minecraft.util.TriState.FALSE, false))
                        .setOutputState(new net.minecraft.client.renderer.RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
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

    private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, ChatScreenshot.ScreenshotStyle style) {
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        OverrideVertexProvider consumer = new OverrideVertexProvider(new com.mojang.blaze3d.vertex.ByteBufferBuilder(256), rt);
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, consumer);
        // This variant's vertex provider only serves the text layer, so context.fill(...) would
        // crash on a missing vertex format. Paint the background by clearing the target instead.
        int clearColor = style.background() ? ChatScreenshot.backgroundColor(mc) : 0x00000000;
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), clearColor);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height, 1f);
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                consumer.beginHead(info.getSkin().texture());
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, y - 1, 8);
                consumer.endHead();
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                net.minecraft.util.FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
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
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, y - 1, 8);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                net.minecraft.util.FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
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
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState, 0, 0);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, y - 1, 8);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                net.minecraft.util.FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
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
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            //? if >=26.2 {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget("chatting_screenshot", width * scale, height * scale, false, com.mojang.blaze3d.GpuFormat.RGBA8_UNORM);
            //?} else {
            /*rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
            *///?}
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        net.minecraft.client.renderer.state.gui.GuiRenderState renderState = new net.minecraft.client.renderer.state.gui.GuiRenderState();
        net.minecraft.client.gui.GuiGraphicsExtractor context = new net.minecraft.client.gui.GuiGraphicsExtractor(mc, renderState, 0, 0);
        //? if >=26.2 {
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), new org.joml.Vector4f(0.0F, 0.0F, 0.0F, 0.0F));
        //?} else {
        /*com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        *///?}
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceExtractor.extractRenderState(context, info.getSkin(), m, y - 1, 8);
            }
            int hx = ChatScreenshot.headOffset(line.content()) + m;
            if (style.border()) {
                net.minecraft.util.FormattedCharSequence bl = ChatScreenshot.blackOut(line.content());
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

    //? if >=1.21.5 <1.21.6 {
    /*private static void readbackAndPersist(RenderTarget rt) {
        int i = rt.width, j = rt.height;
        com.mojang.blaze3d.textures.GpuTexture tex = rt.getColorTexture();
        int px = tex.getFormat().pixelSize();
        com.mojang.blaze3d.systems.GpuDevice device = com.mojang.blaze3d.systems.RenderSystem.getDevice();
        com.mojang.blaze3d.buffers.GpuBuffer buffer = device.createBuffer(null, com.mojang.blaze3d.buffers.BufferType.PIXEL_PACK, com.mojang.blaze3d.buffers.BufferUsage.STATIC_READ, i * j * px);
        com.mojang.blaze3d.systems.CommandEncoder encoder = device.createCommandEncoder();
        encoder.copyTextureToBuffer(tex, buffer, 0, () -> {
            try (com.mojang.blaze3d.buffers.GpuBuffer.ReadView view = device.createCommandEncoder().readBuffer(buffer)) {
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
        com.mojang.blaze3d.textures.GpuTexture tex = rt.getColorTexture();
        //? if >=26.2 {
        int px = tex.getFormat().blockSize();
        //?} else {
        /*int px = tex.getFormat().pixelSize();
        *///?}
        com.mojang.blaze3d.systems.GpuDevice device = com.mojang.blaze3d.systems.RenderSystem.getDevice();
        com.mojang.blaze3d.buffers.GpuBuffer buffer = device.createBuffer(null, com.mojang.blaze3d.buffers.GpuBuffer.USAGE_COPY_DST | com.mojang.blaze3d.buffers.GpuBuffer.USAGE_MAP_READ, i * j * px);
        com.mojang.blaze3d.systems.CommandEncoder encoder = device.createCommandEncoder();
        encoder.copyTextureToBuffer(tex, buffer, 0, () -> {
            //? if >=26.2 {
            try (com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView view = buffer.map(true, false)) {
            //?} else {
            /*try (com.mojang.blaze3d.buffers.GpuBuffer.MappedView view = encoder.mapBuffer(buffer, true, false)) {
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
