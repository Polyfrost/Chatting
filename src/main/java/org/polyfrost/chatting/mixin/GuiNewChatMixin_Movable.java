package org.polyfrost.chatting.mixin;

import org.polyfrost.oneconfig.hud.Position;
import org.polyfrost.oneconfig.internal.hud.HudCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.polyfrost.chatting.chat.ChatWindow;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.utils.ModCompatHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_Movable {

    @Shadow public abstract int getChatWidth();

    @Unique private static ChatLine chatting$currentLine;

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

    @Inject(method = "drawChat", at = @At(value = "HEAD"), cancellable = true)
    private void exampleChat(int updateCounter, CallbackInfo ci) {
        if (HudCore.editing) ci.cancel();
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 3))
    private int mouseX(int constant) {
        ChattingConfig config = ChattingConfig.INSTANCE;
        ChatWindow hud = config.getChatWindow();
        return (int) ((hud.position.getX()) + (hud.getPaddingX() + 1) * hud.getScale());
    }

    @ModifyConstant(method = "getChatComponent", constant = @Constant(intValue = 27))
    private int mouseY(int constant) {
        int height = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        return height - (int) (hud.position.getBottomY() - hud.getPaddingY() * hud.getScale() + ModCompatHooks.getChatPosition());
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

    @ModifyArg(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_float(F)I", ordinal = 2))
    private float width(float value) {
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        return hud.position.getX() + hud.getWidth();
    }

}