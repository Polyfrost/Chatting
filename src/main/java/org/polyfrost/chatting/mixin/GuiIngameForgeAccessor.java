package org.polyfrost.chatting.mixin;

import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngameForge.class)
public interface GuiIngameForgeAccessor {
    @Invoker("renderChat")
    void drawChat(int width, int height);
}
