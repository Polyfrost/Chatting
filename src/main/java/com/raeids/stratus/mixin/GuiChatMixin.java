package com.raeids.stratus.mixin;

import com.raeids.stratus.config.StratusConfig;
import com.raeids.stratus.hook.ChatHookKt;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (StratusConfig.INSTANCE.getChatSearch()) {
            ChatHookKt.initGui();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;drawTextBox()V", shift = At.Shift.AFTER))
    private void yeah(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ChatHookKt.getInputField() != null) {
            ChatHookKt.getInputField().drawTextBox();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("TAIL"))
    private void onGuiClosed(CallbackInfo ci) {
        ChatHookKt.setInputField(null);
        ChatHookKt.setPrevText("");
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        ChatHookKt.updateScreen();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChatHookKt.getInputField() != null) {
            if (ChatHookKt.getInputField().isFocused()) {
                ci.cancel();
                if (keyCode == 1 && ChatHookKt.getInputField().isFocused()) {
                    ChatHookKt.getInputField().setFocused(false);
                    return;
                }
                ChatHookKt.getInputField().textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (ChatHookKt.getInputField() != null) {
            ChatHookKt.getInputField().mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
