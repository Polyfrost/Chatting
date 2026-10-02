package org.polyfrost.chatting.mixin;

//? if >=1.21.11 {
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if <26 {
/*import net.minecraft.client.gui.render.state.GuiTextRenderState;
*///?} else {
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
//?}

@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static boolean chatting$noShadow(boolean dropShadow) {
        return dropShadow && !Chatting.noShadowPass;
    }
}
//?}
//? if <1.21.11 {
/*public class GuiTextRenderStateMixin {
}
*///?}
