package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiTextField;
import org.polyfrost.chatting.chat.ChatHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin {

    @ModifyVariable(method = "drawTextBox", at = @At("STORE"), ordinal = 0)
    private String reset(String string) {
        if (string.isEmpty() && ChatHooks.INSTANCE.checkField(this)) ChatHooks.INSTANCE.setInput("");
        return string;
    }

    @ModifyArgs(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0))
    private void captureString(Args args) {
        if (ChatHooks.INSTANCE.checkField(this)) ChatHooks.INSTANCE.setInput(args.get(0));
    }

    @ModifyArgs(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 1))
    private void captureString2(Args args) {
        if (ChatHooks.INSTANCE.checkField(this)) ChatHooks.INSTANCE.setInput(args.get(0));
    }
}
