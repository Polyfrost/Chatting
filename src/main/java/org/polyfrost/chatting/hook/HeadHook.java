package org.polyfrost.chatting.hook;

//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
//? if >= 1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
 *///?}

public interface HeadHook {
    void chatting$draw(/*? if >= 26.1 {*/ GuiGraphicsExtractor /*?} else {*/ /*GuiGraphics *//*?}*/ graphics, /*? if >= 1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation *//*?}*/ texture, int x, int y, int size, int color, boolean hatVisible, boolean upsideDown);
}
