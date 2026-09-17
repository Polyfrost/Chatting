package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.polyfrost.chatting.chat.ChatShortcuts;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.chat.ChatCopyButton;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.ClearButton;
import org.polyfrost.chatting.gui.components.SearchButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla GuiChat wiring for Chatting's controls, search and shortcuts. */
@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {
    @Shadow protected GuiTextField inputField;
    @Unique private SearchButton chatting$searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void chatting$init(CallbackInfo ci) {
        chatting$initButtons();
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void chatting$updateScreen(CallbackInfo ci) {
        if (chatting$searchButton != null && chatting$searchButton.isEnabled()) chatting$searchButton.getInputField().updateCursorCounter();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void chatting$keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (chatting$searchButton != null && chatting$searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == Keyboard.KEY_ESCAPE) {
                chatting$searchButton.onMousePress();
            } else {
                chatting$searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
                ChatSearchingManager.INSTANCE.setLastSearch(chatting$searchButton.getInputField().getText());
            }
        } else if (GuiScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_TAB) {
            chatting$switchTab();
        }
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String chatting$modifySentMessage(String original) {
        return ChattingConfig.INSTANCE.getChatShortcuts() && original.startsWith("/")
            ? "/" + ChatShortcuts.INSTANCE.handleSentCommand(original.substring(1))
            : original;
    }

    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    private void chatting$beginSmoothScroll(CallbackInfo ci) {
        ChatScrolling.INSTANCE.setShouldSmooth(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatting$copyHoveredLine(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton != 0 || !ChattingConfig.INSTANCE.getChatCopy()) return;
        String text = ChatCopyButton.consumeHoveredText();
        if (text == null) return;
        GuiScreen.setClipboardString(text);
        ci.cancel();
    }

    @Unique
    private void chatting$initButtons() {
        chatting$searchButton = new SearchButton();
        if (ChattingConfig.INSTANCE.getChatSearch()) buttonList.add(chatting$searchButton);
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) buttonList.add(new ClearButton());
        if (ChattingConfig.INSTANCE.getChatTabs()) ChatTabs.INSTANCE.getTabs().forEach(tab -> buttonList.add(tab.getButton()));
    }

    @Unique
    private void chatting$switchTab() {
        if (ChatTabs.INSTANCE.getTabs().isEmpty()) return;
        org.polyfrost.chatting.chat.ChatTab current = ChatTabs.INSTANCE.getCurrentTabs().isEmpty() ? null : ChatTabs.INSTANCE.getCurrentTabs().get(0);
        int next = current == null ? 0 : (ChatTabs.INSTANCE.getTabs().indexOf(current) + 1) % ChatTabs.INSTANCE.getTabs().size();
        ChatTabs.INSTANCE.getCurrentTabs().clear();
        ChatTabs.INSTANCE.getCurrentTabs().add(ChatTabs.INSTANCE.getTabs().get(next));
    }
}
