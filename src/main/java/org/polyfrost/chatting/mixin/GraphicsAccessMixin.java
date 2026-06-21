package org.polyfrost.chatting.mixin;

//? if >=1.21.11 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.chat.ChatHeads;
import org.polyfrost.chatting.chat.SmoothChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if <26 {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
//?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
//?}

@Mixin(targets = {
    "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess",
    "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess"
})
*///?}
public class GraphicsAccessMixin {

    //? if >=1.21.11 <26 {
    /*@Unique private GuiGraphics chatting$graphics;
    @Unique private boolean chatting$shift;
    @Unique private FormattedCharSequence chatting$lineSeq;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void chatting$captureGraphics(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics graphics) {
        this.chatting$graphics = graphics;
    }

    @Inject(method = "handleMessage", at = @At("HEAD"))
    private void chatting$head(int textTop, float opacity, FormattedCharSequence seq, CallbackInfoReturnable<Boolean> cir) {
        Chatting.noShadowPass = ChattingConfig.INSTANCE.getTextRenderType() == 0;
        chatting$lineSeq = seq;
        chatting$shift = false;
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return;
        PlayerInfo info = ChatHeads.INSTANCE.lookup(seq);
        boolean hidden = ChatHeads.INSTANCE.isHidden(seq);
        if (ChatHeads.INSTANCE.shouldDrawHead(info, hidden)) {
            int alpha = Math.round(SmoothChat.INSTANCE.fade(seq, opacity) * 255f);
            PlayerFaceRenderer.draw(this.chatting$graphics, info.getSkin(), 0, textTop - 1, 8, 0xFFFFFF | (alpha << 24));
        }
        chatting$shift = ChatHeads.INSTANCE.shouldOffset(info);
    }

    @Inject(method = "handleMessage", at = @At("RETURN"))
    private void chatting$endMessage(int textTop, float opacity, FormattedCharSequence seq, CallbackInfoReturnable<Boolean> cir) {
        Chatting.noShadowPass = false;
    }

    @ModifyArg(
        method = "handleMessage",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/client/gui/ActiveTextCollector$Parameters;Lnet/minecraft/util/FormattedCharSequence;)V"),
        index = 1
    )
    private int chatting$shiftText(int x) {
        return chatting$shift ? x + 10 : x;
    }

    @ModifyArg(
        method = "handleMessage",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector$Parameters;withOpacity(F)Lnet/minecraft/client/gui/ActiveTextCollector$Parameters;"),
        index = 0
    )
    private float chatting$fade(float opacity) {
        return SmoothChat.INSTANCE.fade(chatting$lineSeq, opacity);
    }
    *///?}

    //? if >=26 {
    /*@Unique private GuiGraphicsExtractor chatting$graphics;
    @Unique private boolean chatting$shift;
    @Unique private FormattedCharSequence chatting$lineSeq;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void chatting$captureGraphics(CallbackInfo ci, @Local(argsOnly = true) GuiGraphicsExtractor graphics) {
        this.chatting$graphics = graphics;
    }

    @Inject(method = "handleMessage", at = @At("HEAD"))
    private void chatting$head(int textTop, float opacity, FormattedCharSequence seq, CallbackInfoReturnable<Boolean> cir) {
        Chatting.noShadowPass = ChattingConfig.INSTANCE.getTextRenderType() == 0;
        chatting$lineSeq = seq;
        chatting$shift = false;
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return;
        PlayerInfo info = ChatHeads.INSTANCE.lookup(seq);
        boolean hidden = ChatHeads.INSTANCE.isHidden(seq);
        if (ChatHeads.INSTANCE.shouldDrawHead(info, hidden)) {
            int alpha = Math.round(SmoothChat.INSTANCE.fade(seq, opacity) * 255f);
            PlayerFaceExtractor.extractRenderState(this.chatting$graphics, info.getSkin(), 0, textTop - 1, 8, 0xFFFFFF | (alpha << 24));
        }
        chatting$shift = ChatHeads.INSTANCE.shouldOffset(info);
    }

    @Inject(method = "handleMessage", at = @At("RETURN"))
    private void chatting$endMessage(int textTop, float opacity, FormattedCharSequence seq, CallbackInfoReturnable<Boolean> cir) {
        Chatting.noShadowPass = false;
    }

    @ModifyArg(
        method = "handleMessage",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/client/gui/ActiveTextCollector$Parameters;Lnet/minecraft/util/FormattedCharSequence;)V"),
        index = 1
    )
    private int chatting$shiftText(int x) {
        return chatting$shift ? x + 10 : x;
    }

    @ModifyArg(
        method = "handleMessage",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector$Parameters;withOpacity(F)Lnet/minecraft/client/gui/ActiveTextCollector$Parameters;"),
        index = 0
    )
    private float chatting$fade(float opacity) {
        return SmoothChat.INSTANCE.fade(chatting$lineSeq, opacity);
    }
    *///?}
}
