package org.polyfrost.chatting.chat;

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
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}

//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;
//?}

//? if =1.21.5 {
/*import com.mojang.renderpearl.api.buffers.BufferType;
import com.mojang.renderpearl.api.buffers.BufferUsage;
import net.minecraft.client.renderer.RenderType;
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

    //? if =1.21.5 {
    /*private static final java.util.function.Function<RenderTarget, RenderType> CUSTOM_TEXT_LAYER = (rt) -> RenderType.create(
            "chatting_text", 786432, false, false,
            net.minecraft.client.renderer.RenderPipelines.TEXT,
            RenderType.CompositeState.builder()
                    .setTextureState(net.minecraft.client.renderer.RenderStateShard.NO_TEXTURE)
                    .setOutputState(new net.minecraft.client.renderer.RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                    .setLightmapState(net.minecraft.client.renderer.RenderStateShard.LIGHTMAP)
                    .createCompositeState(false));

    private static final java.util.function.Function<RenderTarget, RenderType> CUSTOM_SOLID_LAYER = (rt) -> RenderType.create(
            "chatting_solid", 786432, false, false,
            net.minecraft.client.renderer.RenderPipelines.GUI,
            RenderType.CompositeState.builder()
                    .setOutputState(new net.minecraft.client.renderer.RenderStateShard.OutputStateShard("chatting_fbo", () -> rt))
                    .createCompositeState(false));

    // heads blit through RenderType.guiTextured whose vertex format lacks the lightmap element the shared text layer needs so they get their own layer
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

        private final RenderType solidLayer;

        private RenderType headLayer;
        private com.mojang.blaze3d.vertex.BufferBuilder headBuffer;
        private com.mojang.blaze3d.vertex.BufferBuilder solidBuffer;

        private OverrideVertexProvider(com.mojang.blaze3d.vertex.ByteBufferBuilder allocator, RenderTarget rt) {
            super(allocator, it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps.emptyMap());
            this.rt = rt;
            this.solidLayer = CUSTOM_SOLID_LAYER.apply(rt);
            this.textLayer = CUSTOM_TEXT_LAYER.apply(rt);
            this.textBuffer = new com.mojang.blaze3d.vertex.BufferBuilder(this.sharedBuffer, textLayer.mode(), textLayer.format());
            this.headAllocator = new com.mojang.blaze3d.vertex.ByteBufferBuilder(256);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer getBuffer(RenderType renderType) {
            if (this.headBuffer != null) return this.headBuffer;
            return this.solidBuffer != null ? this.solidBuffer : this.textBuffer;
        }

        // route following blits into a dedicated head layer sharing our framebuffer because each head has its own skin texture and must flush first
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

        public void beginSolid() {
            this.solidBuffer = new com.mojang.blaze3d.vertex.BufferBuilder(this.headAllocator, solidLayer.mode(), solidLayer.format());
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
        OverrideVertexProvider consumer = new OverrideVertexProvider(new com.mojang.blaze3d.vertex.ByteBufferBuilder(256), rt);
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, consumer);
        // this variant's vertex provider only serves the text layer so context.fill would crash on a missing vertex format and we clear the target instead
        int clearColor = style.background() ? ChatScreenshot.backgroundColor(mc) : 0x00000000;
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), clearColor);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height, 1f);
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                int hy = ChatHeads.INSTANCE.headY(y);
                float dy = ChatHeads.INSTANCE.headYFraction();
                if (dy != 0f) context.pose().translate(0f, dy, 0f);
                boolean legacy = ChatHeads.INSTANCE.isLegacyShadow();
                if (ChatHeads.INSTANCE.shouldDrawShadow() && legacy) {
                    consumer.beginSolid();
                    context.fill(m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, m + ChatHeads.SHADOW_OFFSET + 8, hy + ChatHeads.SHADOW_OFFSET + 8, ChatHeads.INSTANCE.shadowColor(info, 255));
                    consumer.endSolid();
                }
                consumer.beginHead(info.getSkin().texture());
                if (ChatHeads.INSTANCE.shouldDrawShadow() && !legacy) net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, 8, ChatHeads.INSTANCE.shadowColor(255));
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, hy, 8);
                if (dy != 0f) context.pose().translate(0f, -dy, 0f);
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
        TextureTarget rt;
        try {
            rt = new TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState);
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                int hy = ChatHeads.INSTANCE.headY(y);
                float dy = ChatHeads.INSTANCE.headYFraction();
                if (dy != 0f) context.pose().translate(0f, dy);
                if (ChatHeads.INSTANCE.shouldDrawShadow()) {
                    if (ChatHeads.INSTANCE.isLegacyShadow()) context.fill(m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, m + ChatHeads.SHADOW_OFFSET + 8, hy + ChatHeads.SHADOW_OFFSET + 8, ChatHeads.INSTANCE.shadowColor(info, 255));
                    else net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, 8, ChatHeads.INSTANCE.shadowColor(255));
                }
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, hy, 8);
                if (dy != 0f) context.pose().translate(0f, -dy);
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
        TextureTarget rt;
        try {
            rt = new TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notifyError("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState, 0, 0);
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        if (style.background()) {
            context.fill(0, 0, width, height, ChatScreenshot.backgroundColor(mc));
        }
        int m = style.border() ? 1 : 0;
        int y = m;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                int hy = ChatHeads.INSTANCE.headY(y);
                float dy = ChatHeads.INSTANCE.headYFraction();
                if (dy != 0f) context.pose().translate(0f, dy);
                if (ChatHeads.INSTANCE.shouldDrawShadow()) {
                    if (ChatHeads.INSTANCE.isLegacyShadow()) context.fill(m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, m + ChatHeads.SHADOW_OFFSET + 8, hy + ChatHeads.SHADOW_OFFSET + 8, ChatHeads.INSTANCE.shadowColor(info, 255));
                    else net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, 8, ChatHeads.INSTANCE.shadowColor(255));
                }
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), m, hy, 8);
                if (dy != 0f) context.pose().translate(0f, -dy);
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
        net.minecraft.client.renderer.state.gui.GuiRenderState renderState = new net.minecraft.client.renderer.state.gui.GuiRenderState();
        net.minecraft.client.gui.GuiGraphicsExtractor context = new net.minecraft.client.gui.GuiGraphicsExtractor(mc, renderState, 0, 0);
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
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                int hy = ChatHeads.INSTANCE.headY(y);
                float dy = ChatHeads.INSTANCE.headYFraction();
                if (dy != 0f) context.pose().translate(0f, dy);
                if (ChatHeads.INSTANCE.shouldDrawShadow()) {
                    if (ChatHeads.INSTANCE.isLegacyShadow()) context.fill(m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, m + ChatHeads.SHADOW_OFFSET + 8, hy + ChatHeads.SHADOW_OFFSET + 8, ChatHeads.INSTANCE.shadowColor(info, 255));
                    else net.minecraft.client.gui.components.PlayerFaceExtractor.extractRenderState(context, info.getSkin(), m + ChatHeads.SHADOW_OFFSET, hy + ChatHeads.SHADOW_OFFSET, 8, ChatHeads.INSTANCE.shadowColor(255));
                }
                net.minecraft.client.gui.components.PlayerFaceExtractor.extractRenderState(context, info.getSkin(), m, hy, 8);
                if (dy != 0f) context.pose().translate(0f, -dy);
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
