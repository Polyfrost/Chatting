package cc.woverflow.chattils.mixin;

import cc.woverflow.chattils.chat.ChatSearchingManager;
import cc.woverflow.chattils.chat.ChatShortcuts;
import cc.woverflow.chattils.chat.ChatTab;
import cc.woverflow.chattils.chat.ChatTabs;
import cc.woverflow.chattils.config.ChattilsConfig;
import cc.woverflow.chattils.gui.components.ScreenshotButton;
import cc.woverflow.chattils.gui.components.SearchButton;
import cc.woverflow.chattils.hook.GuiNewChatHook;
import cc.woverflow.chattils.utils.ModCompatHooks;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {

    @Unique
    private static final List<String> COPY_TOOLTIP = Lists.newArrayList(
            "\u00A73\u00A7l\u00A7nCopy To Clipboard",
            "\u00A7lNORMAL CLICK\u00A7r - Full Message",
            "\u00A7lCTRL CLICK\u00A7r - Single Line",
            "\u00A7lSHIFT CLICK\u00A7r - Screenshot Line",
            "",
            "\u00A73\u00A7l\u00A7nModifiers",
            "\u00A7lALT\u00A7r - Formatting Codes");

    private SearchButton searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (ChattilsConfig.INSTANCE.getChatSearch()) {
            searchButton = new SearchButton();
            buttonList.add(searchButton);
        }
        buttonList.add(new ScreenshotButton());
        if (ChattilsConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
        }
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        if (ChattilsConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            searchButton.getInputField().updateCursorCounter();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChattilsConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == 1) {
                searchButton.onMousePress();
                return;
            }
            searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
            ChatSearchingManager.setPrevText(searchButton.getInputField().getText());
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.shouldCopy() && (hook.getRight() + ModCompatHooks.getXOffset()) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 9 > x) {
            GuiUtils.drawHoveringText(COPY_TOOLTIP, mouseX, mouseY, width, height, -1, fontRendererObj);
            GlStateManager.disableLighting();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.shouldCopy() && (hook.getRight() + ModCompatHooks.getXOffset()) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 9 > x) {
            Transferable message = hook.getChattilsChatComponent(Mouse.getY());
            if (message == null) return;
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(message, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String modifySentMessage(String original) {
        if (ChattilsConfig.INSTANCE.getChatShortcuts()) {
            if (original.startsWith("/")) {
                return "/" + ChatShortcuts.INSTANCE.handleSentCommand(StringUtils.substringAfter(original, "/"));
            }
        }
        return original;
    }
}
