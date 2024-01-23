package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.hud.Position;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.polyfrost.chatting.chat.ChatWindow;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.utils.ModCompatHooks;
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
        args.set(0, position.getX() + hud.getPaddingX() * hud.getScale());
        args.set(1, position.getBottomY() - hud.getPaddingY() * hud.getScale());
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

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 3))
    private int mouseX(int constant) {
        ChattingConfig config = ChattingConfig.INSTANCE;
        ChatWindow hud = config.getChatWindow();
        return (int) ((hud.position.getX()) + (hud.getPaddingX() + 1 + (config.getShowChatHeads() && config.getOffsetNonPlayerMessages() ? 8 : 0)) * hud.getScale());
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 27))
    private int mouseY(int constant) {
        int height = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        return height - (int) (hud.position.getBottomY() - hud.getPaddingY() * hud.getScale() + ModCompatHooks.getChatPosition());
    }

    @Redirect(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatScale()F", ordinal = 0))
    private float getScale(GuiNewChat instance) {
        return ChattingConfig.INSTANCE.getChatWindow().getScale();
    }

    @Redirect(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatScale()F", ordinal = 1))
    private float getScale2(GuiNewChat instance) {
        return 1f;
    }

}