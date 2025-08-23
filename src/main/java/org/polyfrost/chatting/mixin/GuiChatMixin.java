package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.libs.universal.*;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.*;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.*;
import org.polyfrost.chatting.hook.*;
import org.polyfrost.chatting.utils.ModCompatHooks;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen implements GuiChatHook {
    @Shadow
    protected GuiTextField inputField;

    @Shadow
    public abstract void drawScreen(int mouseX, int mouseY, float partialTicks);

    @Shadow
    private String defaultInputFieldText;

    /**
     * Gets the modifier key name depending on the operating system
     *
     * @return "OPTION" if macOS, otherwise, "ALT"
     */
    @Unique
    private static String chatting$getModifierKey() {
        return (UDesktop.isMac()) ? "OPTION" : "ALT";
    }

    @Unique
    private static final List<String> COPY_TOOLTIP = Lists.newArrayList(
            "§e§lCopy To Clipboard",
            "§b§lNORMAL CLICK§r §8- §7Full Message",
            "§b§lCTRL CLICK§r §8- §7Single Line",
            "§b§lSHIFT CLICK§r §8- §7Screenshot Message",
            "",
            "§e§lModifiers",
            "§b§l" + chatting$getModifierKey() + "§r §8- §7Formatting Codes"
    );

    @Unique
    private static final List<String> DELETE_TOOLTIP = Lists.newArrayList(
            "§b§lNORMAL CLICK§r §8- §7Full Message",
            "§b§lCTRL CLICK§r §8- §7Single Line"
    );

    @Unique
    private SearchButton chatting$searchButton;
    @Unique
    private ScreenshotButton chatting$screenshotButton;
    @Unique
    private ClearButton chatting$clearButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        chatting$initButtons();
        if (ChattingConfig.INSTANCE.getChatInput().getInputFieldDraft()) {
            String command = (ChatHooks.INSTANCE.getCommandDraft().startsWith("/") ? "" : "/") + ChatHooks.INSTANCE.getCommandDraft();
            inputField.setText(inputField.getText().startsWith("/") ? command : ChatHooks.INSTANCE.getDraft());
        }
        ChatHooks.INSTANCE.setTextField(inputField);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && chatting$searchButton.isEnabled()) {
            chatting$searchButton.getInputField().updateCursorCounter();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && chatting$searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == 1) {
                chatting$searchButton.onMousePress();
                return;
            }
            chatting$searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
            ChatSearchingManager.INSTANCE.setLastSearch(chatting$searchButton.getInputField().getText());
        } else if ((Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) || Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && keyCode == UKeyboard.KEY_TAB) { // either macos super key or ctrl key for any os
            ChatHooks.INSTANCE.switchTab();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        boolean copy = ChattingConfig.INSTANCE.getChatCopy();
        boolean delete = ChattingConfig.INSTANCE.getChatDelete();
        if (!copy && !delete) return;
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        int scale = new ScaledResolution(mc).getScaleFactor();
        int x = Mouse.getX();
        int right = (int) ((hook.chatting$getRight() + ModCompatHooks.getXOffset() + 1 + hud.getPaddingX() * (ChattingConfig.INSTANCE.getExtendBG() ? 1f : 2f)) * hud.getScale() + (int) hud.position.getX());
        delete = delete && chatting$hovered(hook, x, right + (int) ((copy ? 10 : 0) * hud.getScale()), scale, hud);
        copy = copy && chatting$hovered(hook, x, right, scale, hud);

        if (copy || delete) {
            List<String> tooltip = delete ? DELETE_TOOLTIP : COPY_TOOLTIP;
            GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, width, height, -1, fontRendererObj);
            GlStateManager.disableLighting();
        }
    }

    @Unique
    private boolean chatting$hovered(GuiNewChatHook hook, int x, int right, int scale, ChatWindow hud) {
        return hook.chatting$isHovering() && x > right * scale && x < (right + 9 * hud.getScale()) * scale;
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;drawRect(IIIII)V"))
    private void cancelBG(int left, int top, int right, int bottom, int color) {
        ChattingConfig config = ChattingConfig.INSTANCE;
        ChatHooks.INSTANCE.setInputBoxRight(config.getChatInput().getCompactInputBox() ? Math.max((int) config.getChatWindow().getWidth() + 2, ChatHooks.INSTANCE.getInputRight() + (inputField.getText().length() < ModCompatHooks.getChatInputLimit() ? 8 : 2)) : width - 2);
        config.getChatInput().drawBG();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        int scale = new ScaledResolution(mc).getScaleFactor();
        int x = Mouse.getX();
        if (hook.chatting$isHovering()) {
            boolean copy = ChattingConfig.INSTANCE.getChatCopy();
            int right = (int) ((hook.chatting$getRight() + ModCompatHooks.getXOffset() + 1 + hud.getPaddingX() * (ChattingConfig.INSTANCE.getExtendBG() ? 1f : 2f)) * hud.getScale() + (int) hud.position.getX()) * scale;
            if (copy && x > right && x < right + 9 * hud.getScale() * scale || (mouseButton == 1 && ChattingConfig.INSTANCE.getRightClickCopy() && (!ChattingConfig.INSTANCE.getRightClickCopyCtrl() || UKeyboard.isCtrlKeyDown()))) {
                Transferable message = hook.chatting$getChattingChatComponent(Mouse.getY(), mouseButton);
                if (message == null) return;
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ChattingConfig.INSTANCE.getChatDelete() && x > right + (copy ? 10 : 0) * hud.getScale() * scale && x < right + ((copy ? 10 : 0) + 9) * hud.getScale() * scale) {
                ChatLine chatLine = hook.chatting$getHoveredLine(Mouse.getY());
                if (chatLine == null) return;
                ModCompatHooks.getDrawnChatLines().removeIf(line -> chatting$remove(line, chatLine));
                ModCompatHooks.getChatLines().removeIf(line -> chatting$remove(line, chatLine));
            }
        }
    }

    @Unique
    private boolean chatting$remove(ChatLine line, ChatLine chatLine) {
        return UKeyboard.isCtrlKeyDown() ?
                ((ChatLineHook) line).chatting$getUniqueId() == ((ChatLineHook) chatLine).chatting$getUniqueId() :
                ((ChatLineHook) ((ChatLineHook) line).chatting$getFullMessage()).chatting$getUniqueId() == ((ChatLineHook) ((ChatLineHook) chatLine).chatting$getFullMessage()).chatting$getUniqueId();
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String modifySentMessage(String original) {
        if (ChattingConfig.INSTANCE.getChatShortcuts()) {
            if (original.startsWith("/")) {
                return "/" + ChatShortcuts.INSTANCE.handleSentCommand(StringUtils.substringAfter(original, "/"));
            }
        }
        return original;
    }

    @Inject(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"))
    private void clearDraft(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatInput().getInputFieldDraft()) {
            inputField.setText(inputField.getText().startsWith("/") ? "/" : "");
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void saveDraft(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatInput().getInputFieldDraft()) {
            if (inputField.getText().startsWith("/")) {
                ChatHooks.INSTANCE.setCommandDraft(inputField.getText());
            } else {
                if (inputField.getText().isEmpty() && defaultInputFieldText.equals("/")) return;
                ChatHooks.INSTANCE.setDraft(inputField.getText());
            }
        }
    }


    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    private void handleMouseInput(CallbackInfo ci) {
        ChatScrollingHook.INSTANCE.setShouldSmooth(true);
    }

    @Unique
    private void chatting$initButtons() {
        chatting$searchButton = new SearchButton();
        if (ChattingConfig.INSTANCE.getChatSearch()) {
            buttonList.add(chatting$searchButton);
        }
        chatting$screenshotButton = new ScreenshotButton();
        if (ChattingConfig.INSTANCE.getChatScreenshot()) {
            buttonList.add(chatting$screenshotButton);
        }
        chatting$clearButton = new ClearButton();
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) {
            buttonList.add(chatting$clearButton);
        }
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
        }
    }

    @Override
    public void chatting$triggerButtonReset() {
        buttonList.removeIf(button -> button instanceof CleanButton);
        chatting$initButtons();
    }
}
