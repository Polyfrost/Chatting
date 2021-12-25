package com.raeids.stratus.mixin;

import com.raeids.stratus.Stratus;
import com.raeids.stratus.config.StratusConfig;
import com.raeids.stratus.hook.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
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

    private CleanSearchButton searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (StratusConfig.INSTANCE.getChatSearch()) {
            searchButton = new CleanSearchButton();
            buttonList.add(searchButton);
        }
        if (StratusConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
        }
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        if (StratusConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            searchButton.getInputField().updateCursorCounter();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (StratusConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == 1) {
                searchButton.onMousePress();
                return;
            }
            searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
            ChatSearchingKt.setPrevText(searchButton.getInputField().getText());
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.shouldCopy() && (hook.getRight() + (Stratus.INSTANCE.isBetterChat() ? ModCompatHooks.getXOffset() : 0)) <= x && (hook.getRight() + (Stratus.INSTANCE.isBetterChat() ? ModCompatHooks.getXOffset() : 0)) + 9 > x) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hook.getStratusChatComponent(Mouse.getY())), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String modifySentMessage(String original) {
        if (StratusConfig.INSTANCE.getChatShortcuts()) {
            if (original.startsWith("/")) {
                return "/" + ChatShortcuts.INSTANCE.handleSentCommand(StringUtils.substringAfter(original, "/"));
            }
        }
        return original;
    }
}
