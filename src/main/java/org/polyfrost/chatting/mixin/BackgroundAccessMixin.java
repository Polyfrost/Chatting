package org.polyfrost.chatting.mixin;

//? if >=1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.polyfrost.chatting.chat.RoundedChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//? if <26 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess")
public class BackgroundAccessMixin {

    // A fresh DrawingBackgroundGraphicsAccess is constructed per render pass, so no reset is needed.
    @Unique private boolean chatting$sawLineFill;

    //? if <26 {
    /*@WrapOperation(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    private void chatting$roundedFill(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        // Line backgrounds are the only fills through this method with x1 == -4; the line loop
        // iterates top to bottom with faded lines skipped, and the bottom line ends at chatBottom.
        boolean chatting$lineFill = x1 == -4;
        boolean chatting$top = chatting$lineFill && !chatting$sawLineFill;
        boolean chatting$bottom = chatting$lineFill && y2 == RoundedChat.chatBottom(graphics.guiHeight());
        if (chatting$lineFill) chatting$sawLineFill = true;
        RoundedChat.fill((a, b, c, d, e) -> original.call(graphics, a, b, c, d, e), (chatting$factor, chatting$body) -> {
            graphics.pose().pushMatrix();
            graphics.pose().scale(chatting$factor, chatting$factor);
            chatting$body.run();
            graphics.pose().popMatrix();
        }, x1, y1, x2, y2, color, chatting$top, chatting$bottom);
    }
    *///?} else {
    @WrapOperation(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void chatting$roundedFill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        // Line backgrounds are the only fills through this method with x1 == -4; the line loop
        // iterates top to bottom with faded lines skipped, and the bottom line ends at chatBottom.
        boolean chatting$lineFill = x1 == -4;
        boolean chatting$top = chatting$lineFill && !chatting$sawLineFill;
        boolean chatting$bottom = chatting$lineFill && y2 == RoundedChat.chatBottom(graphics.guiHeight());
        if (chatting$lineFill) chatting$sawLineFill = true;
        RoundedChat.fill((a, b, c, d, e) -> original.call(graphics, a, b, c, d, e), (chatting$factor, chatting$body) -> {
            graphics.pose().pushMatrix();
            graphics.pose().scale(chatting$factor, chatting$factor);
            chatting$body.run();
            graphics.pose().popMatrix();
        }, x1, y1, x2, y2, color, chatting$top, chatting$bottom);
    }
    //?}
}
//?}
//? if <1.21.11 {
/*public class BackgroundAccessMixin {
}
*///?}
