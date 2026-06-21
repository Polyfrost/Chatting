package org.polyfrost.chatting.chat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

import java.util.List;

//? if >=26 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;*/
//?} else {
import net.minecraft.client.GuiMessage;
//?}

//? if >=1.21.5 <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;*/
//?}

public final class ChatScreenshotModern {

    private ChatScreenshotModern() {
    }

    //? if >=1.21.5 {
    /*static void capture(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        RenderTarget rt = render(mc, lines, width, height, scale, shadow);
        if (rt == null) return;
        readbackAndPersist(rt);
    }*/
    //?}

    //? if >=1.21.6 {
    /*
    private static void chatting$flush(Minecraft mc, Object renderState, RenderTarget rt) {
        net.minecraft.client.gui.render.GuiRenderer guiRenderer = mc.gameRenderer.guiRenderer;
        org.polyfrost.chatting.hook.GuiRendererInterface gri = (org.polyfrost.chatting.hook.GuiRendererInterface) (Object) guiRenderer;
        Object saved = gri.chatting$getRenderState();
        gri.chatting$setRenderState(renderState);
        gri.chatting$render(mc.gameRenderer.fogRenderer.getBuffer(net.minecraft.client.renderer.fog.FogRenderer.FogMode.NONE), rt);
        gri.chatting$setRenderState(saved);
    }*/
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

    private static class OverrideVertexProvider extends net.minecraft.client.renderer.MultiBufferSource.BufferSource {
        private final RenderType currentLayer;
        public com.mojang.blaze3d.vertex.BufferBuilder bufferBuilder;

        private OverrideVertexProvider(com.mojang.blaze3d.vertex.ByteBufferBuilder allocator, RenderTarget rt) {
            super(allocator, it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps.emptyMap());
            this.currentLayer = CUSTOM_TEXT_LAYER.apply(rt);
            this.bufferBuilder = new com.mojang.blaze3d.vertex.BufferBuilder(this.sharedBuffer, currentLayer.mode(), currentLayer.format());
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer getBuffer(RenderType renderType) {
            return this.bufferBuilder;
        }

        public void finishDrawing() {
            this.startedBuilders.put(this.currentLayer, this.bufferBuilder);
            this.endBatch(this.currentLayer);
        }
    }

    private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notify("Chat window is empty.");
            return null;
        }
        OverrideVertexProvider consumer = new OverrideVertexProvider(new com.mojang.blaze3d.vertex.ByteBufferBuilder(256), rt);
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, consumer);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height, 1f);
        int y = 0;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), 0, y - 1, 8);
            }
            context.drawString(mc.font, line.content(), ChatScreenshot.headOffset(line.content()), y, 0xFFFFFFFF, shadow);
            y += 9;
        }
        context.flush();
        consumer.finishDrawing();
        return rt;
    }*/
    //?}

    //? if >=1.21.6 <1.21.11 {
    /*private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notify("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        int y = 0;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), 0, y - 1, 8);
            }
            context.drawString(mc.font, line.content(), ChatScreenshot.headOffset(line.content()), y, 0xFFFFFFFF, shadow);
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }*/
    //?}

    //? if >=1.21.11 <26 {
    /*private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notify("Chat window is empty.");
            return null;
        }
        net.minecraft.client.gui.render.state.GuiRenderState renderState = new net.minecraft.client.gui.render.state.GuiRenderState();
        net.minecraft.client.gui.GuiGraphics context = new net.minecraft.client.gui.GuiGraphics(mc, renderState, 0, 0);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        int y = 0;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceRenderer.draw(context, info.getSkin(), 0, y - 1, 8);
            }
            context.drawString(mc.font, line.content(), ChatScreenshot.headOffset(line.content()), y, 0xFFFFFFFF, shadow);
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }*/
    //?}

    //? if >=26 {
    /*private static RenderTarget render(Minecraft mc, List<GuiMessage.Line> lines, int width, int height, int scale, boolean shadow) {
        com.mojang.blaze3d.pipeline.TextureTarget rt;
        try {
            rt = new com.mojang.blaze3d.pipeline.TextureTarget(null, width * scale, height * scale, false);
        } catch (IllegalArgumentException e) {
            ChatScreenshot.notify("Chat window is empty.");
            return null;
        }
        net.minecraft.client.renderer.state.gui.GuiRenderState renderState = new net.minecraft.client.renderer.state.gui.GuiRenderState();
        net.minecraft.client.gui.GuiGraphicsExtractor context = new net.minecraft.client.gui.GuiGraphicsExtractor(mc, renderState, 0, 0);
        com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().clearColorTexture(rt.getColorTexture(), 0x00000000);
        context.pose().scale((float) mc.getWindow().getGuiScaledWidth() / width, (float) mc.getWindow().getGuiScaledHeight() / height);
        int y = 0;
        for (GuiMessage.Line line : lines) {
            net.minecraft.client.multiplayer.PlayerInfo info = ChatScreenshot.headToDraw(line.content());
            if (info != null) {
                net.minecraft.client.gui.components.PlayerFaceExtractor.extractRenderState(context, info.getSkin(), 0, y - 1, 8);
            }
            context.text(mc.font, line.content(), ChatScreenshot.headOffset(line.content()), y, 0xFFFFFFFF, shadow);
            y += 9;
        }
        chatting$flush(mc, renderState, rt);
        return rt;
    }*/
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
    }*/
    //?}

    //? if >=1.21.6 {
    /*private static void readbackAndPersist(RenderTarget rt) {
        int i = rt.width, j = rt.height;
        com.mojang.blaze3d.textures.GpuTexture tex = rt.getColorTexture();
        int px = tex.getFormat().pixelSize();
        com.mojang.blaze3d.systems.GpuDevice device = com.mojang.blaze3d.systems.RenderSystem.getDevice();
        com.mojang.blaze3d.buffers.GpuBuffer buffer = device.createBuffer(null, com.mojang.blaze3d.buffers.GpuBuffer.USAGE_COPY_DST | com.mojang.blaze3d.buffers.GpuBuffer.USAGE_MAP_READ, i * j * px);
        com.mojang.blaze3d.systems.CommandEncoder encoder = device.createCommandEncoder();
        encoder.copyTextureToBuffer(tex, buffer, 0, () -> {
            try (com.mojang.blaze3d.buffers.GpuBuffer.MappedView view = encoder.mapBuffer(buffer, true, false)) {
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
    }*/
    //?}
}
