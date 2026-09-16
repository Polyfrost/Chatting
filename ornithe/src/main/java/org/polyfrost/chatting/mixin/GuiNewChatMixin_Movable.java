package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.utils.ModCompatHooks;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.polyfrost.polyui.component.Drawable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Movable {

    @Shadow
    public abstract int getChatWidth();

    @Unique
    private static ChatLine chatting$currentLine;

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 0))
    private void translate(Args args) {
        Drawable hud = Chatting.INSTANCE.getChatWindow().get();
        args.set(0, hud.getX() + hud.getPadding().getX() * hud.getScaleX());
        args.set(1, hud.getY() + hud.getHeight() - hud.getPadding().getY() * hud.getScaleY());
    }

    @ModifyVariable(method = "drawChat", at = @At(value = "STORE", ordinal = 0), ordinal = 4)
    private int width(int value) {
        return MathHelper.ceiling_float_int((float) this.getChatWidth());
    }

    @ModifyConstant(method = "drawChat", constant = @Constant(intValue = 9, ordinal = 0))
    private int chatMode(int constant) {
        return constant;
    }

    @Inject(method = "drawChat", at = @At(value = "HEAD"), cancellable = true)
    private void exampleChat(int updateCounter, CallbackInfo ci) {
        if (HudManager.isPanelOpen()) ci.cancel();
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 3))
    private int mouseX(int constant) {
        Drawable hud = Chatting.INSTANCE.getChatWindow().get();
        return (int) ((hud.getX()) + (hud.getPadding().getX() + 1) * hud.getScaleX());
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 27))
    private int mouseY(int constant) {
        int height = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        Drawable hud = Chatting.INSTANCE.getChatWindow().get();
        return height - (int) (hud.getY() + hud.getHeight() - hud.getPadding().getY() * hud.getScaleY() + ModCompatHooks.getChatPosition());
    }

    @ModifyVariable(method = "getChatComponent", at = @At("STORE"), ordinal = 0)
    private ChatLine capture(ChatLine chatLine) {
        chatting$currentLine = chatLine;
        return chatLine;
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 0))
    private int offset(int value) {
        return ((ChatLineHook) chatting$currentLine).chatting$hasDetected() || ChattingConfig.INSTANCE.getOffsetNonPlayerMessages() ? ModCompatHooks.getChatHeadOffset() : 0;
    }

    @Redirect(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatScale()F", ordinal = 0))
    private float getScale(GuiNewChat instance) {
        return Chatting.INSTANCE.getChatWindow().get().getScaleX();
    }

    @ModifyArg(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_float(F)I", ordinal = 2))
    private float width(float value) {
        Drawable hud = Chatting.INSTANCE.getChatWindow().get();
        return hud.getX() + hud.getWidth();
    }

}