package org.polyfrost.chatting.hook;

//? if >=1.21.6 {
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;

public interface GuiRendererInterface {
    void chatting$render(GpuBufferSlice fogBuffer, RenderTarget renderTarget);

    Object chatting$getRenderState();

    void chatting$setRenderState(Object renderState);
}
//?}
