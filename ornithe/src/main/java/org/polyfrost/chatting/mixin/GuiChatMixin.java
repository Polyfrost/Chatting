package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatShortcuts;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.chat.ChatCopyButton;
import org.polyfrost.chatting.chat.ChatDeleteButton;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.ClearButton;
import org.polyfrost.chatting.gui.components.SearchButton;
import org.polyfrost.chatting.gui.components.ScreenshotButton;
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
        if (mouseButton == 0 && ChattingConfig.INSTANCE.getChatDelete()) {
            ChatLine line = ChatDeleteButton.consume();
            if (line != null) {
                chatting$deleteLine(line);
                ci.cancel();
                return;
            }
        }
        if (mouseButton == 0 && ChattingConfig.INSTANCE.getChatCopy()) {
            ChatLine line = ChatCopyButton.consume();
            if (line == null) return;
            // Copy the complete rendered line, including its formatting, rather
            // than the particular text fragment whose draw call found the icon.
            GuiScreen.setClipboardString(line.getChatComponent().getFormattedText());
            ci.cancel();
            return;
        }
        if (mouseButton != 1 || !ChattingConfig.INSTANCE.getRightClickCopy()) return;
        if (ChattingConfig.INSTANCE.getRightClickCopyCtrl() && !GuiScreen.isCtrlKeyDown()) return;
        IChatComponent component = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (component == null) return;
        GuiScreen.setClipboardString(component.getUnformattedText());
        ci.cancel();
    }

    @Unique
    private void chatting$deleteLine(ChatLine line) {
        GuiNewChatAccessor accessor = (GuiNewChatAccessor) mc.ingameGUI.getChatGUI();
        accessor.getDrawnChatLines().remove(line);
        for (java.util.Iterator<ChatLine> it = accessor.getChatLines().iterator(); it.hasNext();) {
            ChatLine candidate = it.next();
            if (candidate.getUpdatedCounter() == line.getUpdatedCounter()
                && candidate.getChatComponent().getFormattedText().equals(line.getChatComponent().getFormattedText())) {
                it.remove();
                break;
            }
        }
    }

    @Unique
    private void chatting$initButtons() {
        chatting$searchButton = new SearchButton();
        if (ChattingConfig.INSTANCE.getChatSearch()) buttonList.add(chatting$searchButton);
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) buttonList.add(new ClearButton());
        if (ChattingConfig.INSTANCE.getChatScreenshot()) buttonList.add(new ScreenshotButton());
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
