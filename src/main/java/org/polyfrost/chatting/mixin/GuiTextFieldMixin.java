package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiTextField;
import org.polyfrost.chatting.chat.ChatHooks;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin {

    @ModifyVariable(method = "drawTextBox", at = @At(value = "STORE"), ordinal = 3)
    private int setX(int value) {
        ChattingConfig config = ChattingConfig.INSTANCE;
        if (ChatHooks.INSTANCE.checkField(this)) return (int) (value / config.getChatWindow().getScale());
        return value;
    }

    @ModifyVariable(method = "drawTextBox", at = @At(value = "STORE"), ordinal = 4)
    private int setY(int value) {
        ChattingConfig config = ChattingConfig.INSTANCE;
        if (ChatHooks.INSTANCE.checkField(this)) return (int) config.getChatInput().getBgTop();
        return value;
    }

    @ModifyVariable(method = "drawTextBox", at = @At(value = "STORE"), ordinal = 5)
    private int getRight(int right) {
        if (ChatHooks.INSTANCE.checkField(this)) ChatHooks.INSTANCE.setInputRight(right);
        return right;
    }
}
