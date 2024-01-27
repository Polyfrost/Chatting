package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.libs.universal.UDesktop;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.*;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.CleanButton;
import org.polyfrost.chatting.gui.components.ClearButton;
import org.polyfrost.chatting.gui.components.ScreenshotButton;
import org.polyfrost.chatting.gui.components.SearchButton;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hook.GuiChatHook;
import org.polyfrost.chatting.hook.GuiNewChatHook;
import org.polyfrost.chatting.utils.ModCompatHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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
        "\u00A7e\u00A7lCopy To Clipboard",
        "\u00A7b\u00A7lNORMAL CLICK\u00A7r \u00A78- \u00A77Full Message",
        "\u00A7b\u00A7lCTRL CLICK\u00A7r \u00A78- \u00A77Single Line",
        "\u00A7b\u00A7lSHIFT CLICK\u00A7r \u00A78- \u00A77Screenshot Line",
        "",
        "\u00A7e\u00A7lModifiers",
        "\u00A7b\u00A7l" + chatting$getModifierKey() + "\u00A7r \u00A78- \u00A77Formatting Codes");

    private SearchButton searchButton;
    private ScreenshotButton screenshotButton;
    private ClearButton clearButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        chatting$initButtons();
        if (ChattingConfig.INSTANCE.getInputFieldDraft()) {
            String command = (ChatHooks.INSTANCE.getCommandDraft().startsWith("/") ? "" : "/") + ChatHooks.INSTANCE.getCommandDraft();
            inputField.setText(inputField.getText().equals("/") ? command : ChatHooks.INSTANCE.getDraft());
        }
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            searchButton.getInputField().updateCursorCounter();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == 1) {
                searchButton.onMousePress();
                return;
            }
            searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
            ChatSearchingManager.INSTANCE.setLastSearch(searchButton.getInputField().getText());
        } else if (isCtrlKeyDown() && keyCode == UKeyboard.KEY_TAB) {
            ci.cancel();
            ChatHooks.INSTANCE.switchTab();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatCopy()) {
            GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
            ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
            int scale = new ScaledResolution(mc).getScaleFactor();
            int x = Mouse.getX();
            int right = (int) ((hook.chatting$getRight() + ModCompatHooks.getXOffset() + 1 + hud.getPaddingX()) * hud.getScale() + (int) hud.position.getX());
            if (hook.chatting$isHovering() && x > right * scale && x < (right + 9 * hud.getScale()) * scale) {
                GuiUtils.drawHoveringText(COPY_TOOLTIP, mouseX, mouseY, width, height, -1, fontRendererObj);
                GlStateManager.disableLighting();
            }
        }
    }

    @ModifyArg(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;drawRect(IIIII)V"), index = 2)
    private int modifyRight(int right) {
        return ChattingConfig.INSTANCE.getCompactInputBox() ? (MathHelper.ceiling_float_int((float) mc.ingameGUI.getChatGUI().getChatWidth() / mc.ingameGUI.getChatGUI().getChatScale()) + 6) : right;
    }

    @ModifyArg(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;drawRect(IIIII)V"), index = 4)
    private int modifyInputBoxColor(int color) {
        return ChattingConfig.INSTANCE.getInputBoxBackgroundColor().getRGB();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        ChatWindow hud = ChattingConfig.INSTANCE.getChatWindow();
        int scale = new ScaledResolution(mc).getScaleFactor();
        int x = Mouse.getX();
        if (hook.chatting$isHovering()) {
            int right = (int) ((hook.chatting$getRight() + ModCompatHooks.getXOffset() + 1 + hud.getPaddingX()) * hud.getScale() + (int) hud.position.getX()) * scale;
            if (ChattingConfig.INSTANCE.getChatCopy() && x > right && x < right + 9 * hud.getScale() * scale || (mouseButton == 1 && ChattingConfig.INSTANCE.getRightClickCopy())) {
                Transferable message = hook.chatting$getChattingChatComponent(Mouse.getY());
                if (message == null) return;
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ChattingConfig.INSTANCE.getChatDelete() && x > right + 10 * hud.getScale() * scale && x < right + 19 * hud.getScale() * scale) {
                ChatLine chatLine = hook.chatting$getHoveredLine(Mouse.getY());
                if (chatLine == null) return;
                ModCompatHooks.getDrawnChatLines().removeIf(line -> ((ChatLineHook) line).chatting$getUniqueId() == ((ChatLineHook) chatLine).chatting$getUniqueId());
                ModCompatHooks.getChatLines().removeIf(line -> ((ChatLineHook) line).chatting$getUniqueId() == ((ChatLineHook) chatLine).chatting$getUniqueId());
            }
        }
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
        if (ChattingConfig.INSTANCE.getInputFieldDraft()) {
            inputField.setText(inputField.getText().startsWith("/") ? "/" : "");
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void saveDraft(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getInputFieldDraft()) {
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
        searchButton = new SearchButton();
        if (ChattingConfig.INSTANCE.getChatSearch()) {
            buttonList.add(searchButton);
        }
        screenshotButton = new ScreenshotButton();
        if (ChattingConfig.INSTANCE.getChatScreenshot()) {
            buttonList.add(screenshotButton);
        }
        clearButton = new ClearButton();
        if (ChattingConfig.INSTANCE.getChatDeleteHistory()) {
            buttonList.add(clearButton);
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
