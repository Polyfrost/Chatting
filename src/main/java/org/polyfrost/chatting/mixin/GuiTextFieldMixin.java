package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.polyfrost.chatting.chat.ChatHooks;
import org.polyfrost.chatting.hook.GuiTextFieldHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin {
    @ModifyVariable(method = "drawTextBox", at = @At(value = "STORE"), ordinal = 5)
    private int getRight(int right) {
        if (ChatHooks.INSTANCE.checkField(this)) ChatHooks.INSTANCE.setInputRight(right);
        return right;
    }

    // TODO: this method is called 3 times, here I'm targetting them all but I'm not really sure if that's required
    @Redirect(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color) {
        return GuiTextFieldHook.redirectDrawString(text, x, y, color);
    }
}
