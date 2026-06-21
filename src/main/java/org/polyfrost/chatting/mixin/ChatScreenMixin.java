package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.ChatSearch;
import org.polyfrost.chatting.chat.ChatScreenshot;
import org.polyfrost.chatting.chat.ChatShortcuts;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.ChatTabsRenderer;
import org.polyfrost.chatting.chat.Textures;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hud.ChatWindowHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//? if >=26 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.gui.GuiGraphicsExtractor;*/
//?} else {
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >=1.21.10 {
/*import net.minecraft.client.input.MouseButtonEvent;*/
//?}
//? if >=1.21.11 {
/*import net.minecraft.util.Mth;*/
//?}

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Unique private boolean chatting$leftClicked;
    @Unique private boolean chatting$rightClicked;
    @Unique private boolean chatting$ctrlHeld;
    @Unique private boolean chatting$shiftHeld;
    @Unique private boolean chatting$altHeld;

    @Unique private List<String> chatting$tooltip;

    @Unique private static final boolean CHATTING$MAC = System.getProperty("os.name", "").toLowerCase().contains("mac");

    @Unique private static final List<String> CHATTING$COPY_TOOLTIP = List.of(
            "§e§lCopy To Clipboard",
            "§b§lNORMAL CLICK§r §8- §7Full Message",
            "§b§lCTRL CLICK§r §8- §7Single Line",
            "§b§lSHIFT CLICK§r §8- §7Screenshot Message",
            "",
            "§e§lModifiers",
            "§b§l" + (CHATTING$MAC ? "OPTION" : "ALT") + "§r §8- §7Formatting Codes"
    );

    @Unique private static final List<String> CHATTING$DELETE_TOOLTIP = List.of(
            "§b§lNORMAL CLICK§r §8- §7Full Message",
            "§b§lCTRL CLICK§r §8- §7Single Line"
    );

    @Unique private EditBox chatting$searchBox;

    @Inject(method = "init", at = @At("TAIL"))
    private void chatting$initSearch(CallbackInfo ci) {
        int boxWidth = width / 4;
        int buttonRow = 3 * (ChatButtons.BUTTON_WIDTH + 2) + 12;
        EditBox box = new EditBox(this.font, width - boxWidth - buttonRow, height - 26, boxWidth, 12, Component.empty());
        box.setMaxLength(100);
        box.setResponder(ChatSearch.INSTANCE::setQuery);
        box.setVisible(ChatSearch.INSTANCE.getEnabled());
        addRenderableWidget(box);
        chatting$searchBox = box;
        chatting$syncSearchBox();
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void chatting$closeSearch(CallbackInfo ci) {
        ChatSearch.INSTANCE.close();
    }

    @Unique
    private void chatting$toggleSearch() {
        ChatSearch.INSTANCE.toggle();
        chatting$syncSearchBox();
    }

    @Unique
    private void chatting$syncSearchBox() {
        if (chatting$searchBox == null) return;
        boolean on = ChatSearch.INSTANCE.getEnabled();
        chatting$searchBox.setVisible(on);
        chatting$searchBox.setValue(ChatSearch.INSTANCE.getQuery());
        if (on) {
            chatting$searchBox.setFocused(true);
            this.setFocused(chatting$searchBox);
            ChatSearch.INSTANCE.refresh();
        } else {
            chatting$searchBox.setFocused(false);
        }
    }

    @ModifyVariable(method = "handleChatInput", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String chatting$applyShortcuts(String message) {
        return ChatTabs.INSTANCE.applyPrefix(ChatShortcuts.INSTANCE.handleSentMessage(message));
    }

    //? if >=26 {
    /*@Inject(method = "extractRenderState", at = @At("TAIL"))
    private void chatting$renderTabs(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "render", at = @At("TAIL"))
    private void chatting$renderTabs(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    //?}
        ChatTabsRenderer.INSTANCE.draw(graphics, mouseX, mouseY);
        chatting$tooltip = null;
        chatting$lineButtons(graphics, mouseX, mouseY);
        chatting$globalButtons(graphics, mouseX, mouseY);
        chatting$drawTooltip(graphics, mouseX, mouseY);
        chatting$leftClicked = false;
        chatting$rightClicked = false;
    }

    @Unique
    private void chatting$lineButtons(Object g0, int mouseX, int mouseY) {
        //? if >=26 {
        /*GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) g0;*/
        //?} else {
        GuiGraphics graphics = (GuiGraphics) g0;
        //?}
        ChattingConfig cfg = ChattingConfig.INSTANCE;
        boolean perLine = ChatButtons.hasPerLineButtons();
        if (!perLine && !cfg.getRightClickCopy()) return;

        ChatComponent chat = minecraft.gui.getChat();
        ChatComponentAccessor acc = (ChatComponentAccessor) chat;
        float chatScale = (float) acc.chatting$getScale();
        if (chatScale <= 0f) return;

        int mx = (int) ChatWindowHud.mapMouseX(mouseX);
        int my = (int) ChatWindowHud.mapMouseY(mouseY);

        int lineIndex = chatting$hoveredVisualLine(chat, acc, my);
        int messageIndex = chatting$hoveredMessageIndex(chat, acc, lineIndex);
        if (messageIndex == -1) return;

        if (chatting$rightClicked && cfg.getRightClickCopy()
                && (!cfg.getRightClickCopyCtrl() || chatting$ctrlHeld)) {
            chatting$copyLine(acc, messageIndex, lineIndex);
        }

        if (!perLine) return;

        int lineHeight = acc.chatting$getLineHeight();
        int localRight = (int) (acc.chatting$getWidth() / chatScale);
        int top = chatting$chatBottomLocal(chatScale) - (lineIndex + 1) * lineHeight
                + (int) Math.ceil((lineHeight - 9) / 2.0);

        boolean hud = ChatWindowHud.isActive();
        float hudScale = ChatWindowHud.chatScale();
        //? if <1.21.6 {
        graphics.pose().pushPose();
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY(), 0f);
            if (hudScale != 1f) graphics.pose().scale(hudScale, hudScale, 1f);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop(), 0f);
        }
        graphics.pose().scale(chatScale, chatScale, 1f);
        //?} else {
        /*graphics.pose().pushMatrix();
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY());
            if (hudScale != 1f) graphics.pose().scale(hudScale, hudScale);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop());
        }
        graphics.pose().scale(chatScale, chatScale);
        *///?}

        int slot = 0;
        if (cfg.getChatCopy()) {
            chatting$button(graphics, Textures.COPY, localRight + 2 + slot * (ChatButtons.BUTTON_WIDTH + 1), top,
                    chatScale, mx, my, CHATTING$COPY_TOOLTIP, () -> chatting$copyAction(acc, messageIndex, lineIndex));
            slot++;
        }
        if (cfg.getChatDelete()) {
            chatting$button(graphics, Textures.DELETE, localRight + 2 + slot * (ChatButtons.BUTTON_WIDTH + 1), top,
                    chatScale, mx, my, CHATTING$DELETE_TOOLTIP, () -> chatting$deleteAction(acc, lineIndex));
        }

        //? if <1.21.6 {
        graphics.pose().popPose();
        //?} else {
        /*graphics.pose().popMatrix();*/
        //?}
    }

    @Unique
    private List<GuiMessage.Line> chatting$entryLines(ChatComponentAccessor acc, int messageIndex) {
        List<GuiMessage.Line> visible = acc.chatting$getTrimmedMessages();
        ArrayList<GuiMessage.Line> parts = new ArrayList<>();
        if (messageIndex < 0 || messageIndex >= visible.size()) return parts;
        parts.add(visible.get(messageIndex));
        for (int i = messageIndex + 1; i < visible.size(); i++) {
            if (visible.get(i).endOfEntry()) break;
            parts.add(0, visible.get(i));
        }
        return parts;
    }

    @Unique
    private void chatting$copyLine(ChatComponentAccessor acc, int messageIndex, int lineIndex) {
        ChatScreenshot.copyText(chatting$entryLines(acc, messageIndex), chatting$messageForLine(acc, lineIndex), false);
    }

    @Unique
    private void chatting$copyAction(ChatComponentAccessor acc, int messageIndex, int lineIndex) {
        if (chatting$shiftHeld) {
            ChatScreenshot.copyImage(chatting$entryLines(acc, messageIndex));
            return;
        }
        boolean fmt = chatting$altHeld;
        if (chatting$ctrlHeld) {
            List<GuiMessage.Line> visible = acc.chatting$getTrimmedMessages();
            int idx = lineIndex + acc.chatting$getScrollbarPos();
            if (idx < 0 || idx >= visible.size()) return;
            ChatScreenshot.copyText(Collections.singletonList(visible.get(idx)), null, fmt);
        } else {
            ChatScreenshot.copyText(chatting$entryLines(acc, messageIndex), chatting$messageForLine(acc, lineIndex), fmt);
        }
    }

    @Unique
    private void chatting$deleteAction(ChatComponentAccessor acc, int lineIndex) {
        if (chatting$ctrlHeld) {
            List<GuiMessage.Line> visible = acc.chatting$getTrimmedMessages();
            int idx = lineIndex + acc.chatting$getScrollbarPos();
            if (idx >= 0 && idx < visible.size()) visible.remove(idx);
            return;
        }
        chatting$deleteForLine(acc, lineIndex);
    }

    @Unique
    private Component chatting$messageForLine(ChatComponentAccessor acc, int lineIndex) {
        lineIndex += acc.chatting$getScrollbarPos();
        List<GuiMessage.Line> trimmed = acc.chatting$getTrimmedMessages();
        List<GuiMessage> all = acc.chatting$getAllMessages();
        int fullIndex = -1;
        for (int i = 0; i < trimmed.size(); i++) {
            if (trimmed.get(i).endOfEntry()) fullIndex++;
            if (i != lineIndex) continue;
            if (fullIndex >= 0 && fullIndex < all.size()) return all.get(fullIndex).content();
            break;
        }
        return null;
    }

    @Unique
    private void chatting$deleteForLine(ChatComponentAccessor acc, int lineIndex) {
        lineIndex += acc.chatting$getScrollbarPos();
        List<GuiMessage.Line> trimmed = acc.chatting$getTrimmedMessages();
        List<GuiMessage> all = acc.chatting$getAllMessages();
        int fullIndex = -1;
        for (int i = 0; i < trimmed.size(); i++) {
            if (trimmed.get(i).endOfEntry()) fullIndex++;
            if (i != lineIndex) continue;
            if (fullIndex >= 0 && fullIndex < all.size()) {
                all.remove(fullIndex);
                acc.chatting$refreshTrimmedMessages();
            }
            return;
        }
    }

    @Unique
    private void chatting$globalButtons(Object graphics, int mouseX, int mouseY) {
        ChattingConfig cfg = ChattingConfig.INSTANCE;
        ChatComponent chat = minecraft.gui.getChat();
        ChatComponentAccessor acc = (ChatComponentAccessor) chat;

        int x = width - 12;
        int y = height - 26;
        if (cfg.getChatScreenshot()) {
            chatting$button(graphics, Textures.SCREENSHOT, x, y, 1f, mouseX, mouseY, null,
                    () -> ChatScreenshot.copyImage(chatting$visibleLines(chat, acc)));
            x -= ChatButtons.BUTTON_WIDTH + 2;
        }
        if (cfg.getChatDeleteHistory()) {
            chatting$button(graphics, Textures.DELETE, x, y, 1f, mouseX, mouseY, null,
                    () -> chat.clearMessages(false));
            x -= ChatButtons.BUTTON_WIDTH + 2;
        }
        if (cfg.getChatSearch()) {
            chatting$button(graphics, Textures.SEARCH, x, y, 1f, mouseX, mouseY, null, this::chatting$toggleSearch);
        }
    }

    @Unique
    private List<GuiMessage.Line> chatting$visibleLines(ChatComponent chat, ChatComponentAccessor acc) {
        List<GuiMessage.Line> visible = acc.chatting$getTrimmedMessages();
        int scrolled = acc.chatting$getScrollbarPos();
        ArrayList<GuiMessage.Line> lines = new ArrayList<>();
        for (int i = scrolled; i < visible.size() && i < chat.getLinesPerPage() + scrolled; i++) {
            lines.add(visible.get(i));
        }
        Collections.reverse(lines);
        return lines;
    }

    @Unique
    private void chatting$button(Object g0, Object icon, int localX, int localY, float scale,
                                 int mouseX, int mouseY, List<String> tooltip, Runnable action) {
        //? if >=26 {
        /*GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) g0;*/
        //?} else {
        GuiGraphics graphics = (GuiGraphics) g0;
        //?}
        ChattingConfig cfg = ChattingConfig.INSTANCE;
        int screenX = (int) (localX * scale);
        int screenY = (int) (localY * scale);
        int screenSize = (int) (ChatButtons.BUTTON_WIDTH * scale);
        boolean hover = mouseX >= screenX && mouseX <= screenX + screenSize
                && mouseY >= screenY && mouseY <= screenY + screenSize;

        int bg = (hover ? cfg.getChatButtonHoveredBackgroundColor() : cfg.getChatButtonBackgroundColor()).getArgb();
        graphics.fill(localX, localY, localX + ChatButtons.BUTTON_WIDTH, localY + ChatButtons.BUTTON_WIDTH, bg);
        chatting$blit(graphics, icon, localX, localY);

        if (hover) {
            if (tooltip != null) chatting$tooltip = tooltip;
            if (chatting$leftClicked) action.run();
        }
    }

    @Unique
    private void chatting$drawTooltip(Object g0, int mouseX, int mouseY) {
        List<String> lines = chatting$tooltip;
        chatting$tooltip = null;
        if (lines == null || lines.isEmpty()) return;
        //? if >=26 {
        /*GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) g0;*/
        //?} else {
        GuiGraphics graphics = (GuiGraphics) g0;
        //?}
        Font font = this.font;
        int textW = 0;
        for (String s : lines) textW = Math.max(textW, font.width(s));
        int pad = 4;
        int lineH = 10;
        int boxW = textW + pad * 2;
        int boxH = lines.size() * lineH - 2 + pad * 2;
        int x = mouseX + 12;
        int y = mouseY - 12;
        if (x + boxW > this.width) x = Math.max(0, mouseX - 12 - boxW);
        if (y + boxH > this.height) y = Math.max(0, this.height - boxH);
        if (y < 0) y = 0;
        graphics.fill(x, y, x + boxW, y + boxH, 0xF0100010);
        int ty = y + pad;
        for (String s : lines) {
            chatting$text(g0, s, x + pad, ty);
            ty += lineH;
        }
    }

    @Unique
    private void chatting$text(Object g0, String s, int x, int y) {
        //? if >=26 {
        /*((GuiGraphicsExtractor) g0).text(this.font, s, x, y, 0xFFFFFFFF);
        *///?} else {
        ((GuiGraphics) g0).drawString(this.font, s, x, y, 0xFFFFFFFF);
        //?}
    }

    @Unique
    private void chatting$blit(Object g0, Object icon, int x, int y) {
        //? if >=26 {
        /*GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) g0;*/
        //?} else {
        GuiGraphics graphics = (GuiGraphics) g0;
        //?}
        //? if <1.21.4 {
        graphics.blit((net.minecraft.resources.ResourceLocation) icon, x, y, 0f, 0f, 9, 9, 9, 9);
        //?} elif <1.21.6 {
        /*graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, (net.minecraft.resources.ResourceLocation) icon, x, y, 0f, 0f, 9, 9, 9, 9);
        *///?} elif <1.21.11 {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, (net.minecraft.resources.ResourceLocation) icon, x, y, 0, 0, 9, 9, 9, 9);
        *///?} else {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, (net.minecraft.resources.Identifier) icon, x, y, 0, 0, 9, 9, 9, 9);
        *///?}
    }

    @Unique
    private int chatting$hoveredVisualLine(ChatComponent chat, ChatComponentAccessor acc, int mouseY) {
        //? if <1.21.11 {
        return (int) acc.chatting$screenToChatY(mouseY);
        //?} else {
        /*double d = (double) this.minecraft.getWindow().getGuiScaledHeight() - mouseY - 40.0;
        return (int) (d / (acc.chatting$getScale() * acc.chatting$getLineHeight()));
        *///?}
    }

    @Unique
    private int chatting$hoveredMessageIndex(ChatComponent chat, ChatComponentAccessor acc, int lineIndex) {
        //? if <1.21.11 {
        return acc.chatting$getMessageEndIndexAt(0, lineIndex);
        //?} else {
        /*if (!chat.isChatFocused()) return -1;
        List<GuiMessage.Line> trimmed = acc.chatting$getTrimmedMessages();
        int i = Math.min(chat.getLinesPerPage(), trimmed.size());
        if (!(lineIndex >= 0 && lineIndex < i)) return -1;
        int j = Mth.floor(lineIndex + acc.chatting$getScrollbarPos());
        if (j < 0 || j >= trimmed.size()) return -1;
        while (j >= 0) {
            if (trimmed.get(j).endOfEntry()) return j;
            j--;
        }
        return j;
        *///?}
    }

    @Unique
    private int chatting$chatBottomLocal(float chatScale) {
        return (int) ((height - 40) / chatScale);
    }

    //? if >=1.21.10 {
    /*@Inject(method = "mouseClicked", at = @At("HEAD"))
    private void chatting$captureClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        int button = event.button();
        chatting$ctrlHeld = event.hasControlDown();
        chatting$shiftHeld = event.hasShiftDown();
        chatting$altHeld = event.hasAltDown();
    *///?} else {
    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void chatting$captureClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        chatting$ctrlHeld = Screen.hasControlDown();
        chatting$shiftHeld = Screen.hasShiftDown();
        chatting$altHeld = Screen.hasAltDown();
    //?}
        if (button == 0) chatting$leftClicked = true;
        else if (button == 1) chatting$rightClicked = true;
    }

    //? if >=1.21.10 {
    /*@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatting$clickTabs(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == 0 && ChatTabsRenderer.INSTANCE.click(event.x(), event.y(), event.hasShiftDown())) {
            cir.setReturnValue(true);
        }
    }*/
    //?} else {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatting$clickTabs(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0 && ChatTabsRenderer.INSTANCE.click(mouseX, mouseY, Screen.hasShiftDown())) {
            cir.setReturnValue(true);
        }
    }
    //?}
}
