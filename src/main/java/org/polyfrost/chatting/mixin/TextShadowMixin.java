package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.Font;
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// the shadowed overload forwards a literal true, so chat can drop the shadow without bypassing other mods' wrappers of the outer call
@Mixin(Font.class)
public class TextShadowMixin {

    @ModifyArg(method = "drawWithShadow(Ljava/lang/String;FFI)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Ljava/lang/String;FFIZ)I"), index = 4)
    private boolean chatting$noShadow(boolean shadow) {
        return shadow && !Chatting.noShadowPass;
    }
}
*///?} elif <1.21.11 {
/*import net.minecraft.client.gui.GuiGraphics;
import org.polyfrost.chatting.Chatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// the shadowed overload forwards a literal true, so chat can drop the shadow without bypassing other mods' wrappers of the outer call
@Mixin(GuiGraphics.class)
public class TextShadowMixin {

    //? if <=1.21.5 {
    /^@ModifyArg(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I"), index = 5)
    ^///?} else {
    @ModifyArg(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V"), index = 5)
    //?}
    private boolean chatting$noShadow(boolean shadow) {
        return shadow && !Chatting.noShadowPass;
    }
}
*///?} else {
public class TextShadowMixin {
}
//?}
