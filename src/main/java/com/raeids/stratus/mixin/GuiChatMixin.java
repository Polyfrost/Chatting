package com.raeids.stratus.mixin;

import com.raeids.stratus.config.StratusConfig;
import com.raeids.stratus.hook.ChatSearchingKt;
import com.raeids.stratus.hook.ChatTab;
import com.raeids.stratus.hook.ChatTabs;
import com.raeids.stratus.hook.GuiNewChatHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (StratusConfig.INSTANCE.getChatSearch()) {
            ChatSearchingKt.initGui();
        }
        if (StratusConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;drawTextBox()V", shift = At.Shift.AFTER))
    private void yeah(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ChatSearchingKt.getInputField() != null) {
            ChatSearchingKt.getInputField().drawTextBox();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("TAIL"))
    private void onGuiClosed(CallbackInfo ci) {
        ChatSearchingKt.setInputField(null);
        ChatSearchingKt.setPrevText("");
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        ChatSearchingKt.updateScreen();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChatSearchingKt.getInputField() != null) {
            if (ChatSearchingKt.getInputField().isFocused()) {
                ci.cancel();
                if (keyCode == 1 && ChatSearchingKt.getInputField().isFocused()) {
                    ChatSearchingKt.getInputField().setFocused(false);
                    return;
                }
                ChatSearchingKt.getInputField().textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (ChatSearchingKt.getInputField() != null) {
            ChatSearchingKt.getInputField().mouseClicked(mouseX, mouseY, mouseButton);
        }
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        if (hook.shouldCopy() && hook.getRight() <= mouseX && hook.getRight() + 9 > mouseX) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hook.getStratusChatComponent(Mouse.getY())), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String modifySentMessage(String original){
        if(original.equalsIgnoreCase ("/pw")){
            return "/p warp";

        }
        return original;

    }
}
