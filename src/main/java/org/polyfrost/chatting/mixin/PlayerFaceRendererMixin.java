package org.polyfrost.chatting.mixin;

//? if >= 26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
//?}
//? if >= 1.21.8 {
/*import net.minecraft.client.renderer.RenderPipelines;
*///?} else {
import net.minecraft.client.renderer.RenderType;
//?}
//? if >= 1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.polyfrost.chatting.hook.HeadHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(/*? if >= 26.1 {*/ /*PlayerFaceExtractor.class *//*?} else {*/ PlayerFaceRenderer.class /*?}*/)
public class PlayerFaceRendererMixin implements HeadHook {
    @Shadow
    private static void /*? if >= 26.1 {*/ /*extractHat *//*?} else {*/ drawHat /*?}*/ (/*? if >= 1.21.11 {*/ /*GuiGraphicsExtractor *//*?} else {*/ GuiGraphics /*?}*/ graphics, /*? if >= 1.21.11 {*/ /*Identifier *//*?} else {*/ ResourceLocation /*?}*/ texture, int x, int y, int size, boolean upsideDown /*? if >= 1.21.4 {*/ /*, int color *//*?}*/) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    @Override
    public void chatting$draw(/*? if >= 1.21.11 {*/ /*GuiGraphicsExtractor *//*?} else {*/ GuiGraphics /*?}*/ graphics, /*? if >= 1.21.11 {*/ /*Identifier *//*?} else {*/ ResourceLocation /*?}*/ texture, int x, int y, int size, int color, boolean hatVisible, boolean upsideDown) {
        int i = 8 + (upsideDown ? 8 : 0);
        int j = 8 * (upsideDown ? -1 : 1);
        graphics.blit(/*? if >= 1.21.8 {*/ /*RenderPipelines.GUI_TEXTURED, *//*?} else if >= 1.21.4 {*/ /*RenderType::guiTextured *//*?}*/ texture, x, y, size, size, 8, /*? if 1.21.1 {*/ (float) /*?}*/ i, 8, j, 64, 64);
        if (hatVisible) {
            graphics.pose().translate(-0.5F, -0.5F /*? if <= 1.21.5 {*/ , 0F /*?}*/);
            /*? if >= 26.1 {*/ /*extractHat *//*?} else {*/ drawHat /*?}*/ (graphics, texture, x , y , 9, upsideDown /*? if >= 1.21.4 {*/ /*, color *//*?}*/);
            graphics.pose().translate(0.5F, 0.5F /*? if <= 1.21.5 {*/ , 0F /*?}*/);
        }
    }
}
