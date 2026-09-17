package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.chat.ChatHooks;
import org.polyfrost.chatting.chat.ChatScrollingHook;
import org.polyfrost.chatting.chat.ChatShortcuts;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.CleanButton;
import org.polyfrost.chatting.gui.components.ClearButton;
import org.polyfrost.chatting.gui.components.ScreenshotButton;
import org.polyfrost.chatting.gui.components.SearchButton;
import org.polyfrost.chatting.hook.GuiChatHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla GuiChat wiring for Chatting's controls, search and shortcuts. */
@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen implements GuiChatHook {
    @Shadow protected GuiTextField inputField;
    @Shadow private String defaultInputFieldText;

    @Unique private SearchButton chatting$searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void chatting$init(CallbackInfo ci) {
        chatting$initButtons();
        if (Chatting.INSTANCE.getChatInput().getInputFieldDraft()) {
            String draft = ChatHooks.INSTANCE.getCommandDraft().startsWith("/") ? ChatHooks.INSTANCE.getCommandDraft() : ChatHooks.INSTANCE.getDraft();
            inputField.setText(draft);
        }
        ChatHooks.INSTANCE.setTextField(inputField);
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
            ChatHooks.INSTANCE.switchTab();
        }
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String chatting$modifySentMessage(String original) {
        return ChattingConfig.INSTANCE.getChatShortcuts() && original.startsWith("/")
            ? "/" + ChatShortcuts.INSTANCE.handleSentCommand(original.substring(1))
            : original;
    }

    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    private void chatting$handleMouseInput(CallbackInfo ci) {
        ChatScrollingHook.INSTANCE.setShouldSmooth(true);
    }

    @Unique
    private void chatting$initButtons() {
        chatting$searchButton = new SearchButton();
        if (ChattingConfig.INSTANCE.getChatSearch()) buttonList.add(chatting$searchButton);
        if (ChattingConfig.INSTANCE.getChatScreenshot()) buttonList.add(new ScreenshotButton());
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) buttonList.add(new ClearButton());
        if (ChattingConfig.INSTANCE.getChatTabs()) ChatTabs.INSTANCE.getTabs().forEach(tab -> buttonList.add(tab.getButton()));
    }

    @Override
    public void chatting$triggerButtonReset() {
        buttonList.removeIf(button -> button instanceof CleanButton);
        chatting$initButtons();
    }
}
