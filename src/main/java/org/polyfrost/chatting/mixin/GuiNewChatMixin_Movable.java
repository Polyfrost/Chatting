package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.hud.Position;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.MathHelper;
import org.polyfrost.chatting.chat.ChatWindow;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Movable {

    @Shadow public abstract int getChatWidth();

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 0))
    private void translate(Args args) {
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        Position position = hud.position;
        args.set(0, position.getX());
        args.set(1, position.getBottomY());
    }

    @ModifyVariable(method = "drawChat", at = @At(value = "STORE", ordinal = 0), ordinal = 4)
    private int width(int value) {
        return MathHelper.ceiling_float_int((float)this.getChatWidth());
    }

    @ModifyConstant(method = "drawChat", constant = @Constant(intValue = 9, ordinal = 0))
    private int chatMode(int constant) {
        return constant;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatScale()F"))
    private float scale(GuiNewChat instance) {
        return ChattingConfig.INSTANCE.getChatWindow().getScale();
    }

    @Inject(method = "drawChat", at = @At(value = "HEAD"), cancellable = true)
    private void exampleChat(int updateCounter, CallbackInfo ci) {
        if (HudCore.editing) ci.cancel();
    }

}