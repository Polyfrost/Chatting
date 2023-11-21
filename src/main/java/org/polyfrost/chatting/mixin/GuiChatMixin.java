package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.chat.*;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.gui.components.ClearButton;
import org.polyfrost.chatting.gui.components.ScreenshotButton;
import org.polyfrost.chatting.gui.components.SearchButton;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hook.GuiNewChatHook;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.ChatShortcuts;
import org.polyfrost.chatting.utils.ModCompatHooks;
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
            "\u00A7e\u00A7lCopy To Clipboard",
            "\u00A7b\u00A7lNORMAL CLICK\u00A7r \u00A78- \u00A77Full Message",
            "\u00A7b\u00A7lCTRL CLICK\u00A7r \u00A78- \u00A77Single Line",
            "\u00A7b\u00A7lSHIFT CLICK\u00A7r \u00A78- \u00A77Screenshot Line",
            "",
            "\u00A7e\u00A7lModifiers",
            "\u00A7b\u00A7lALT\u00A7r \u00A78- \u00A77Formatting Codes");

    private SearchButton searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch()) {
            searchButton = new SearchButton();
            buttonList.add(searchButton);
        }
        buttonList.add(new ScreenshotButton());
        buttonList.add(new ClearButton());
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
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
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.isHovering() && (hook.getRight() + ModCompatHooks.getXOffset() + 3) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 13 > x) {
            GuiUtils.drawHoveringText(COPY_TOOLTIP, mouseX, mouseY, width, height, -1, fontRendererObj);
            GlStateManager.disableLighting();
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
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.isHovering()) {
            if (((hook.getRight() + ModCompatHooks.getXOffset() + 3) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 13 > x) || (mouseButton == 1 && ChattingConfig.INSTANCE.getRightClickCopy())) {
                Transferable message = hook.getChattingChatComponent(Mouse.getY());
                if (message == null) return;
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ((hook.getRight() + ModCompatHooks.getXOffset() + 13) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 23 > x) {
                ChatLine chatLine = hook.getHoveredLine(Mouse.getY());
                if (chatLine == null) return;
                ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getDrawnChatLines().removeIf(line -> ((ChatLineHook) line).getUniqueId() == ((ChatLineHook) chatLine).getUniqueId());
                ((GuiNewChatAccessor) Minecraft.getMinecraft().ingameGUI.getChatGUI()).getChatLines().removeIf(line -> ((ChatLineHook) line).getUniqueId() == ((ChatLineHook) chatLine).getUniqueId());
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

    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    private void handleMouseInput(CallbackInfo ci) {
        ChatScrollingHook.INSTANCE.setShouldSmooth(true);
    }
}
