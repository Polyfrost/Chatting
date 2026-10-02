package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.Chatting;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*//~ if =1.8.9 'net.minecraft.client.GuiMessage' -> 'net.minecraft.client.gui.ChatMessage'
import net.minecraft.client.GuiMessage;
*///?}
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import net.minecraft.util.Mth;
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.ChatDimensions;
import org.polyfrost.chatting.chat.ChatHeads;
import org.polyfrost.chatting.chat.ChatHover;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.chat.ChatSearch;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.RoundedChat;
import org.polyfrost.chatting.chat.SmoothChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Shadow;
import org.polyfrost.chatting.hook.ChatComponentHook;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hud.ChatPreview;
import org.polyfrost.chatting.hud.ChatWindowHud;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
//? if > 1.8.9 <=1.21.10 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.FormattedCharSequence;
*///?}
//? if <26 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.render.platform.GlStateManager;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
*///?}

@Mixin(ChatComponent.class)
public class ChatComponentMixin implements ChatComponentHook {
    @Shadow
    //~ if =1.8.9 'refreshTrimmedMessages' -> 'rescaleChat'
    private void refreshTrimmedMessages() {
        throw new AssertionError();
    }

    @Override
    public void chatting$refresh() {
        //~ if =1.8.9 'refreshTrimmedMessages' -> 'rescaleChat'
        refreshTrimmedMessages();
    }

    //? if = 1.8.9 {
    /*@Shadow @Final private Minecraft minecraft;
    @Shadow @Final private List<ChatMessage> allMessages;
    @Shadow private boolean hasNewMessagesSinceScroll;
    *///?}

    //? if = 1.8.9 {
    /*// 1.8.9 render has no focused parameter
    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    private void chatting$peek(CallbackInfoReturnable<Boolean> cir) {
        if (Chatting.INSTANCE.getPeeking()) cir.setReturnValue(true);
    }
    *///?} elif <=1.21.10 {
    /*@ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean chatting$peek(boolean focused) {
        return focused || Chatting.INSTANCE.getPeeking() || HudManager.INSTANCE.isEditing();
    }
    *///?} elif <26 {
    /*@ModifyVariable(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean chatting$peek(boolean focused) {
        return focused || Chatting.INSTANCE.getPeeking() || HudManager.INSTANCE.isEditing();
    }
    *///?} else {
    @ModifyVariable(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private ChatComponent.DisplayMode chatting$peek(ChatComponent.DisplayMode mode) {
        return (mode == ChatComponent.DisplayMode.BACKGROUND && (Chatting.INSTANCE.getPeeking() || HudManager.INSTANCE.isEditing()))
            ? ChatComponent.DisplayMode.FOREGROUND
            : mode;
    }
    //?}

    @Inject(method = "getWidth()I", at = @At("HEAD"), cancellable = true)
    private void chatting$width(CallbackInfoReturnable<Integer> cir) {
        if (ChattingConfig.INSTANCE.getCustomChatWidth()) {
            cir.setReturnValue(ChatDimensions.width());
        }
    }

    @Inject(method = "getHeight()I", at = @At("HEAD"), cancellable = true)
    private void chatting$height(CallbackInfoReturnable<Integer> cir) {
        boolean focused = ((ChatComponent) (Object) this).isChatFocused();
        boolean peeking = Chatting.INSTANCE.getPeeking() && !focused;
        if (!peeking && !ChattingConfig.INSTANCE.getCustomChatHeight()) return;
        cir.setReturnValue(ChatDimensions.height(focused || peeking));
    }

    //? if >=1.21.11 <26 {
    /*@Unique private boolean chatting$posed;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"), cancellable = true)
    private void chatting$beginChatWindow(GuiGraphics graphics, Font font, int ticks, int mouseX, int mouseY, boolean focused, boolean changeCursor, CallbackInfo ci) {
        if (ChatWindowHud.shouldHideForVisibility(((ChatComponent) (Object) this).isChatFocused())) {
            chatting$posed = false;
            ci.cancel();
            return;
        }
        chatting$installPreview();
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = chatting$previewing ? 0f : SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
        chatting$posed = hud || smoothDy != 0f;
        if (!chatting$posed) return;
        graphics.pose().pushMatrix();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy);
        if (hud) {
            float scale = ChatWindowHud.chatScale();
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY());
            if (scale != 1f) graphics.pose().scale(scale, scale);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop());
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("RETURN"))
    private void chatting$endChatWindow(GuiGraphics graphics, Font font, int ticks, int mouseX, int mouseY, boolean focused, boolean changeCursor, CallbackInfo ci) {
        chatting$restorePreview();
        if (!chatting$posed) return;
        chatting$posed = false;
        graphics.pose().popMatrix();
    }
    *///?} elif >=26 {
    @Unique private boolean chatting$posed;

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"), cancellable = true)
    private void chatting$beginChatWindow(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode mode, boolean changeCursor, CallbackInfo ci) {
        if (ChatWindowHud.shouldHideForVisibility(((ChatComponent) (Object) this).isChatFocused())) {
            chatting$posed = false;
            ci.cancel();
            return;
        }
        chatting$installPreview();
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = chatting$previewing ? 0f : SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
        chatting$posed = hud || smoothDy != 0f;
        if (!chatting$posed) return;
        graphics.pose().pushMatrix();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy);
        if (hud) {
            float scale = ChatWindowHud.chatScale();
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY());
            if (scale != 1f) graphics.pose().scale(scale, scale);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop());
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("RETURN"))
    private void chatting$endChatWindow(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode mode, boolean changeCursor, CallbackInfo ci) {
        chatting$restorePreview();
        if (!chatting$posed) return;
        chatting$posed = false;
        graphics.pose().popMatrix();
    }
    //?}

    @Unique
    private PlayerInfo chatting$pendingHead;
    @Unique
    private boolean chatting$pendingHideHead;
    @Unique
    private boolean chatting$headConsumed;
    @Unique
    private PlayerInfo chatting$lastHeadOwner;

    @Unique
    private boolean chatting$addingMessage;
    @Unique
    private int chatting$scrollPosBefore;

    //? if > 1.8.9 {
    @Unique
    private GuiMessage chatting$currentMessage;

    @Inject(method = "addMessageToDisplayQueue", at = @At("HEAD"), cancellable = true)
    private void chatting$detectHead(GuiMessage guiMessage, CallbackInfo ci) {
        if (ChatTabs.INSTANCE.shouldFilter() && !ChatTabs.INSTANCE.shouldRender((Component) guiMessage.content())) {
            ci.cancel();
            return;
        }
        if (ChatSearch.INSTANCE.shouldFilter() && !ChatSearch.INSTANCE.matches((Component) guiMessage.content())) {
            ci.cancel();
            return;
        }
        chatting$currentMessage = guiMessage;
        chatting$headConsumed = false;
        chatting$pendingHead = ChattingConfig.INSTANCE.getShowChatHeads()
            ? ChatHeads.INSTANCE.detect((Component) guiMessage.content())
            : null;
        chatting$pendingHideHead = ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages()
            && ChatHeads.INSTANCE.sameOwner(chatting$pendingHead, chatting$lastHeadOwner);
        chatting$lastHeadOwner = chatting$pendingHead;
        if (!chatting$refreshing) SmoothChat.INSTANCE.start();
        chatting$addingMessage = true;
        chatting$scrollPosBefore = chatScrollbarPos;
    }

    @ModifyExpressionValue(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private int chatting$headWrapWidth(int width) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads() || !ChatHeads.INSTANCE.shouldOffset(chatting$pendingHead)) return width;
        return width - 10;
    }

    @Inject(method = "addMessageToDisplayQueue", at = @At("RETURN"))
    private void chatting$endDisplayQueue(GuiMessage guiMessage, CallbackInfo ci) {
        if (!chatting$addingMessage) return;
        chatting$addingMessage = false;
        int delta = chatScrollbarPos - chatting$scrollPosBefore;
        if (delta != 0) ChatScrolling.INSTANCE.shift(delta);
    }

    @Unique
    private void chatting$applyHead(Object element) {
        if (!chatting$refreshing) SmoothChat.INSTANCE.addLine(((GuiMessage.Line) element).content());
        ((ChatLineHook) element).chatting$setParent(chatting$currentMessage);
        if (chatting$headConsumed) return;
        chatting$headConsumed = true;
        ((ChatLineHook) element).chatting$setPlayerInfo(chatting$pendingHead);
        ((ChatLineHook) element).chatting$setHeadHidden(chatting$pendingHideHead);
        ChatHeads.INSTANCE.tag(((GuiMessage.Line) element).content(), chatting$pendingHead, chatting$pendingHideHead);
    }
    //?} else {
    /*@Unique
    private int chatting$newLines;

    // 1.8.9 adds history and display lines together, so keep filtered messages in history
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At("HEAD"), cancellable = true)
    private void chatting$detectHead(Component component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if ((ChatTabs.INSTANCE.shouldFilter() && !ChatTabs.INSTANCE.shouldRender(component))
            || (ChatSearch.INSTANCE.shouldFilter() && !ChatSearch.INSTANCE.matches(component))) {
            chatting$keepInHistory(component, chatLineId, updateCounter, displayOnly);
            ci.cancel();
            return;
        }
        chatting$newLines = 0;
        chatting$headConsumed = false;
        chatting$pendingHead = ChattingConfig.INSTANCE.getShowChatHeads()
            ? ChatHeads.INSTANCE.detect(component)
            : null;
        chatting$pendingHideHead = ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages()
            && ChatHeads.INSTANCE.sameOwner(chatting$pendingHead, chatting$lastHeadOwner);
        chatting$lastHeadOwner = chatting$pendingHead;
        if (!chatting$refreshing) SmoothChat.INSTANCE.start();
        chatting$addingMessage = true;
        chatting$scrollPosBefore = chatScrollbarPos;
    }

    @Unique
    private void chatting$keepInHistory(Component component, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) ((ChatComponent) (Object) this).removeMessage(chatLineId);
        if (displayOnly) return;
        allMessages.add(0, new ChatMessage(updateCounter, component, chatLineId));
        while (allMessages.size() > 100) allMessages.remove(allMessages.size() - 1);
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(F)I"))
    private int chatting$headWrapWidth(int width) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads() || !ChatHeads.INSTANCE.shouldOffset(chatting$pendingHead)) return width;
        return width - 10;
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At("RETURN"))
    private void chatting$endDisplayQueue(Component component, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (!chatting$addingMessage) return;
        chatting$addingMessage = false;
        int delta = chatScrollbarPos - chatting$scrollPosBefore;
        if (delta != 0) ChatScrolling.INSTANCE.shift(delta);
        // the history entry only exists after vanilla adds it
        ChatMessage parent = null;
        for (ChatMessage entry : allMessages) {
            if (entry.getText() == component) {
                parent = entry;
                break;
            }
        }
        // wrapped lines are inserted at the front, so index 0 ends the entry
        int count = Math.min(chatting$newLines, trimmedMessages.size());
        for (int i = 0; i < count; i++) {
            ChatLineHook line = (ChatLineHook) trimmedMessages.get(i);
            line.chatting$setParent(parent);
            line.chatting$setEndOfEntry(i == 0);
        }
    }

    @Unique
    private void chatting$applyHead(Object element) {
        chatting$newLines++;
        if (!chatting$refreshing) SmoothChat.INSTANCE.addLine((ChatMessage) element);
        if (chatting$headConsumed) return;
        chatting$headConsumed = true;
        ((ChatLineHook) element).chatting$setPlayerInfo(chatting$pendingHead);
        ((ChatLineHook) element).chatting$setHeadHidden(chatting$pendingHideHead);
        ChatHeads.INSTANCE.tag((ChatMessage) element, chatting$pendingHead, chatting$pendingHideHead);
    }
    *///?}

    @Unique
    private boolean chatting$refreshing;

    @Shadow
    @Final
    //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
    private List<GuiMessage.Line> trimmedMessages;

    @Unique
    private boolean chatting$previewing;

    @Unique
    //~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
    private final List<GuiMessage.Line> chatting$previewBackup = new ArrayList<>();

    @Unique
    private void chatting$installPreview() {
        if (!HudManager.INSTANCE.isEditing()) return;
        chatting$previewing = true;
        chatting$previewBackup.clear();
        chatting$previewBackup.addAll(trimmedMessages);
        trimmedMessages.clear();
        trimmedMessages.addAll(ChatPreview.lines());
    }

    @Unique
    private void chatting$restorePreview() {
        if (!chatting$previewing) return;
        chatting$previewing = false;
        trimmedMessages.clear();
        trimmedMessages.addAll(chatting$previewBackup);
        chatting$previewBackup.clear();
    }

    @Shadow
    private int chatScrollbarPos;

    @Inject(method = "scrollChat", at = @At("HEAD"))
    private void chatting$armSmoothScroll(int amount, CallbackInfo ci) {
        if (chatting$addingMessage) return;
        ChatScrolling.INSTANCE.setShouldSmooth(true);
    }

    //~ if =1.8.9 'refreshTrimmedMessages' -> 'rescaleChat'
    @Inject(method = "refreshTrimmedMessages", at = @At("HEAD"))
    private void chatting$beginRefresh(CallbackInfo ci) {
        chatting$refreshing = true;
        chatting$lastHeadOwner = null;
    }

    //~ if =1.8.9 'refreshTrimmedMessages' -> 'rescaleChat'
    @Inject(method = "refreshTrimmedMessages", at = @At("RETURN"))
    private void chatting$endRefresh(CallbackInfo ci) {
        chatting$refreshing = false;
    }

    //? if = 1.8.9 {
    /*// ordinal 0 is trimmedMessages, ordinal 1 is history
    @Redirect(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    private void chatting$tagHead(List<Object> list, int index, Object element) {
        chatting$applyHead(element);
        list.add(index, element);
    }
    *///?} elif <=1.21.10 {
    /*@Redirect(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V"))
    private void chatting$tagHead(List<Object> list, int index, Object element) {
        chatting$applyHead(element);
        list.add(index, element);
    }
    *///?} else {
    @Redirect(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V"))
    private void chatting$tagHead(List<Object> list, Object element) {
        chatting$applyHead(element);
        list.addFirst(element);
    }
    //?}

    //? if > 1.8.9 <=1.21.10 {
    /*@Unique
    private boolean chatting$posed;

    @Unique
    private boolean chatting$sawLineFill;

    //? if <=1.21.5 {
    /^@Unique
    private int chatting$visibleLines;
    ^///?}

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void chatting$beginChatWindow(GuiGraphics graphics, int tick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        chatting$sawLineFill = false;
        if (ChatWindowHud.shouldHideForVisibility(((ChatComponent) (Object) this).isChatFocused())) {
            chatting$posed = false;
            ci.cancel();
            return;
        }
        chatting$installPreview();
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        //? if <=1.21.5 {
        /^// peek ModifyVariable also targets HEAD and may run after this inject so focused is recomputed here and the OR is idempotent
        chatting$visibleLines = chatting$countVisibleLines(tick,
            focused || Chatting.INSTANCE.getPeeking() || HudManager.INSTANCE.isEditing());
        ^///?}
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = chatting$previewing ? 0f : SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
        chatting$posed = hud || smoothDy != 0f;
        if (!chatting$posed) return;
        float scale = ChatWindowHud.chatScale();
        //? if <1.21.6 {
        /^graphics.pose().pushPose();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy, 0.0F);
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY(), 0.0F);
            if (scale != 1f) graphics.pose().scale(scale, scale, 1.0F);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop(), 0.0F);
        }
        ^///?} else {
        graphics.pose().pushMatrix();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy);
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY());
            if (scale != 1f) graphics.pose().scale(scale, scale);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop());
        }
        //?}
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void chatting$endChatWindow(GuiGraphics graphics, int tick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        chatting$restorePreview();
        if (!chatting$posed) return;
        chatting$posed = false;
        //? if <1.21.6 {
        /^graphics.pose().popPose();
        ^///?} else {
        graphics.pose().popMatrix();
        //?}
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int chatting$renderMouseX(int mouseX) {
        chatting$mouseX = (int) ChatWindowHud.mapMouseX(mouseX);
        return chatting$mouseX;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private int chatting$renderMouseY(int mouseY) {
        chatting$mouseY = (int) ChatWindowHud.mapMouseY(mouseY);
        return chatting$mouseY;
    }

    @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double chatting$styleX(double x) {
        return ChatWindowHud.mapMouseX(x);
    }

    @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double chatting$styleY(double y) {
        return ChatWindowHud.mapMouseY(y);
    }

    @ModifyVariable(method = "getMessageTagAt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double chatting$tagX(double x) {
        return ChatWindowHud.mapMouseX(x);
    }

    @ModifyVariable(method = "getMessageTagAt", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double chatting$tagY(double y) {
        return ChatWindowHud.mapMouseY(y);
    }

    @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double chatting$queueX(double x) {
        return ChatWindowHud.mapMouseX(x);
    }

    @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double chatting$queueY(double y) {
        return ChatWindowHud.mapMouseY(y);
    }
    *///?} elif = 1.8.9 {
    /*@Unique
    private boolean chatting$posed;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void chatting$beginChatWindow(int ticks, CallbackInfo ci) {
        if (ChatWindowHud.shouldHideForVisibility(((ChatComponent) (Object) this).isChatFocused())) {
            chatting$posed = false;
            ci.cancel();
            return;
        }
        chatting$installPreview();
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        chatting$posed = ChatWindowHud.isActive();
        if (!chatting$posed) return;
        // the gui has already translated chat down by scaledHeight - 48
        float guiY = minecraft.getWindow().getGuiScaledHeight() - 48;
        float scale = ChatWindowHud.chatScale();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0F, -guiY, 0.0F);
        GlStateManager.translatef(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY(), 0.0F);
        if (scale != 1f) GlStateManager.scalef(scale, scale, 1.0F);
        GlStateManager.translatef(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop(), 0.0F);
        GlStateManager.translatef(0.0F, guiY, 0.0F);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void chatting$endChatWindow(int ticks, CallbackInfo ci) {
        chatting$restorePreview();
        if (!chatting$posed) return;
        chatting$posed = false;
        GlStateManager.popMatrix();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void chatting$translateNewMessage(int ticks, CallbackInfo ci) {
        GlStateManager.translatef(0f, SmoothChat.INSTANCE.translateY(hasNewMessagesSinceScroll), 0f);
    }

    @ModifyVariable(method = "getMessageAt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int chatting$messageX(int x) {
        if (!ChatWindowHud.isActive()) return x;
        int factor = minecraft.getWindow().getGuiScale();
        return (int) (ChatWindowHud.mapMouseX((double) x / factor) * factor);
    }

    @ModifyVariable(method = "getMessageAt", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int chatting$messageY(int y) {
        if (!ChatWindowHud.isActive()) return y;
        Window window = minecraft.getWindow();
        int factor = window.getGuiScale();
        int height = window.getGuiScaledHeight();
        return (int) ((height - ChatWindowHud.mapMouseY(height - (double) y / factor)) * factor);
    }

    // chat is drawn 28px above the bottom edge but vanilla hit tests from 27
    @ModifyConstant(method = "getMessageAt", constant = @Constant(intValue = 27))
    private int chatting$alignComponentHitTest(int original) {
        return 28;
    }
    *///?}

    //? if > 1.8.9 <=1.21.10 {
    /*@Unique
    private int chatting$mouseX;

    @Unique
    private int chatting$mouseY;

    @Unique
    private int chatting$drawHead(GuiGraphics graphics, GuiMessage.Line line, int x, int y, int alpha) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return x;
        PlayerInfo info = ((ChatLineHook) (Object) line).chatting$getPlayerInfo();
        boolean hidden = ((ChatLineHook) (Object) line).chatting$isHeadHidden();
        if (ChatHeads.INSTANCE.shouldDrawHead(info, hidden)) {
            ChatHeads.INSTANCE.draw(graphics, info, x, y, alpha);
        }
        return ChatHeads.INSTANCE.shouldOffset(info) ? x + 10 : x;
    }

    @Unique
    private void chatting$drawHoverBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, GuiMessage.Line line) {
        boolean focused = ((ChatComponent) (Object) this).isChatFocused();
        if (focused) {
            x2 += ChatButtons.extraBackgroundWidth();
        }
        if (focused && chatting$highlighted(line)) {
            color = ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        } else {
            color = ChatBackground.tint(color);
        }
        int chatBottom = RoundedChat.chatBottom(graphics.guiHeight());
        //? if <=1.21.5 {
        /^// render loop iterates bottom to top so the top line comes from the precomputed visible line count
        int index = (chatBottom - y2) / ((ChatComponentAccessor) (Object) this).chatting$getLineHeight();
        boolean top = index == chatting$visibleLines - 1;
        ^///?} else {
        // forEachLine iterates top to bottom with faded lines skipped so the first fill per render pass is the topmost visible line
        boolean top = !chatting$sawLineFill;
        //?}
        boolean bottom = y2 == chatBottom;
        chatting$sawLineFill = true;
        RoundedChat.fill(graphics::fill, RoundedChat.scaler(graphics.pose()), x1, y1, x2, y2, color, top, bottom);
    }

    // identity rather than indexOf because indexOf collapses duplicate messages
    @Unique
    private boolean chatting$highlighted(GuiMessage.Line line) {
        ChatComponent self = (ChatComponent) (Object) this;
        int start = ChatHover.scrollPos();
        int end = start + ChatHover.visibleRows(self);
        for (int i = start; i < end; i++) {
            if (trimmedMessages.get(i) == line) return ChatHover.highlighted(self, chatting$mouseX, chatting$mouseY, i);
        }
        return false;
    }

    //? if <=1.21.5 {
    /^// replicates the render loop per line visibility gate to find the topmost line whose background fill will run
    @Unique
    private int chatting$countVisibleLines(int tickCount, boolean focused) {
        int perPage = ((ChatComponent) (Object) this).getLinesPerPage();
        int scroll = chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
        double opacity = Minecraft.getInstance().options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612;
        int top = 0;
        for (int i = 0; i + scroll < trimmedMessages.size() && i < perPage; i++) {
            GuiMessage.Line line = trimmedMessages.get(i + scroll);
            if (line == null) continue;
            int added = ChattingConfig.INSTANCE.getFade() ? line.addedTime() - chatting$fadeOffset() : Integer.MAX_VALUE;
            int age = tickCount - added;
            if (!(age < 200 || focused)) continue;
            double factor = focused ? 1.0 : chatting$timeFactor(age);
            if ((int) (255.0 * factor * opacity) > 3) top = i + 1;
        }
        return top;
    }

    // copy of the vanilla render loop getTimeFactor
    @Unique
    private static double chatting$timeFactor(int age) {
        double t = age / 200.0;
        t = 1.0 - t;
        t *= 10.0;
        t = Mth.clamp(t, 0.0, 1.0);
        return t * t;
    }
    ^///?}

    //? if <=1.21.5 {
    /^@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
    private void chatting$hoverBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, @Local GuiMessage.Line line) {
        chatting$drawHoverBackground(graphics, x1, y1, x2, y2, color, line);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I", ordinal = 0))
    private int chatting$renderLine(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color, @Local GuiMessage.Line line) {
        color = SmoothChat.INSTANCE.fadeColor(line.content(), color);
        int dx = chatting$drawHead(graphics, line, x, y, color >>> 24);
        switch (ChattingConfig.INSTANCE.getTextRenderType()) {
            case 0:
                return graphics.drawString(font, text, dx, y, color, false);
            default:
                return graphics.drawString(font, text, dx, y, color);
        }
    }
    ^///?} else {
    
    // method_71991 = line text drawString and method_71992 = line background fill
    @Redirect(method = "method_71992", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
    private void chatting$hoverBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, @Local(argsOnly = true) GuiMessage.Line line) {
        chatting$drawHoverBackground(graphics, x1, y1, x2, y2, color, line);
    }

    @Redirect(method = "method_71991", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)V", ordinal = 0))
    private void chatting$renderLine(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color, @Local(argsOnly = true) GuiMessage.Line line) {
        color = SmoothChat.INSTANCE.fadeColor(line.content(), color);
        int dx = chatting$drawHead(graphics, line, x, y, color >>> 24);
        switch (ChattingConfig.INSTANCE.getTextRenderType()) {
            case 0:
                graphics.drawString(font, text, dx, y, color, false);
                break;
            default:
                graphics.drawString(font, text, dx, y, color);
        }
    }
    //?}
    *///?} elif = 1.8.9 {
    /*@Unique
    private float chatting$drawHead(ChatMessage line, float x, float y, int alpha) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return x;
        PlayerInfo info = ((ChatLineHook) line).chatting$getPlayerInfo();
        boolean hidden = ((ChatLineHook) line).chatting$isHeadHidden();
        if (ChatHeads.INSTANCE.shouldDrawHead(info, hidden)) {
            ChatHeads.INSTANCE.draw(new GuiGraphics(), info, (int) x, (int) y, alpha);
            // heads leave blending off
            GlStateManager.enableBlend();
        }
        return ChatHeads.INSTANCE.shouldOffset(info) ? x + 10.0F : x;
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(IIIII)V", ordinal = 0))
    private void chatting$hoverBackground(Args args, int ticks) {
        int vanillaColor = args.get(4);
        int left = args.get(0);
        int top = args.get(1);
        int right = (int) args.get(2) + chatting$buttonBackgroundWidth();
        int bottom = args.get(3);
        int color = ChatBackground.tint(vanillaColor);
        if (minecraft.screen instanceof ChatScreen && chatting$highlighted(bottom)) {
            color = ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        }
        boolean rounded = ChattingConfig.INSTANCE.getRoundedChatCorners();
        int lineIndex = -bottom / 9;
        int[] bounds = rounded ? chatting$roundedBounds(ticks) : null;
        RoundedChat.fill(GuiElement::fill, RoundedChat.scaler(new PoseStack()), left, top, right, bottom, color, rounded && lineIndex == bounds[1], rounded && lineIndex == bounds[0]);
        args.set(2, right);
        args.set(4, vanillaColor & 0x00FFFFFF);
    }

    // replicates the render loop's per line visibility gate to find the first and last lines whose background fill will run
    @Unique
    private int[] chatting$roundedBounds(int ticks) {
        ChatComponent self = (ChatComponent) (Object) this;
        int visibleLines = Math.min(self.getLinesPerPage(), Math.max(0, trimmedMessages.size() - chatScrollbarPos));
        int first = -1;
        int last = -1;
        boolean keepMessagesVisible = !ChattingConfig.INSTANCE.getFade() || self.isChatFocused() || HudManager.INSTANCE.isEditing();
        float opacity = minecraft.options.chatOpacity * 0.9F + 0.1F;

        for (int lineIndex = 0; lineIndex < visibleLines; lineIndex++) {
            ChatMessage line = trimmedMessages.get(lineIndex + chatScrollbarPos);
            int age = ticks - line.getTimeOfCreation() + chatting$fadeOffset();
            if (age >= 200 && !keepMessagesVisible) continue;

            double fade = 1.0D - age / 200.0D;
            fade = Math.max(0.0D, Math.min(1.0D, fade * 10.0D));
            int alpha = keepMessagesVisible ? 255 : (int) (255.0D * fade * fade);
            if ((int) (alpha * opacity) <= 3) continue;
            if (first < 0) first = lineIndex;
            last = lineIndex;
        }
        return new int[]{first, last};
    }

    @Unique
    private int chatting$buttonBackgroundWidth() {
        if (!ChattingConfig.INSTANCE.getExtendBG() || !(minecraft.screen instanceof ChatScreen)) return 0;
        return ChatButtons.extraBackgroundWidth();
    }

    @Unique
    private boolean chatting$highlighted(int bottom) {
        Window window = minecraft.getWindow();
        int screenX = Mouse.getX() * window.getGuiScaledWidth() / minecraft.width;
        int screenY = window.getGuiScaledHeight() - Mouse.getY() * window.getGuiScaledHeight() / minecraft.height - 1;
        // truncated to match the buttons
        int mouseX = (int) ChatWindowHud.mapMouseX(screenX);
        int mouseY = (int) ChatWindowHud.mapMouseY(screenY);
        return ChatHover.highlighted((ChatComponent) (Object) this, mouseX, mouseY, -bottom / 9 + ChatHover.scrollPos());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawWithShadow(Ljava/lang/String;FFI)I"))
    private int chatting$renderLine(Font font, String text, float x, float y, int color, @Local ChatMessage line) {
        color = SmoothChat.INSTANCE.fadeColor(line, color);
        float dx = chatting$drawHead(line, x, y, color >>> 24);
        return font.draw(text, dx, y, color, ChattingConfig.INSTANCE.getTextRenderType() != 0);
    }
    *///?}

    @Unique
    private int chatting$fadeOffset() {
        return 200 - (int) (ChattingConfig.INSTANCE.getFadeTime() * 20);
    }

    //? if = 1.8.9 {
    /*@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), ordinal = 6)
    private int chatting$fadeAge(int age) {
        return age + chatting$fadeOffset();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z"))
    private boolean chatting$fadeFocused(ChatComponent chat) {
        return !ChattingConfig.INSTANCE.getFade() || chat.isChatFocused() || HudManager.INSTANCE.isEditing();
    }
    *///?} elif <=1.21.5 {
    /*@ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;addedTime()I"))
    private int chatting$fadeAge(int addedTime) {
        if (!ChattingConfig.INSTANCE.getFade()) return Integer.MAX_VALUE;
        return addedTime - chatting$fadeOffset();
    }
    *///?}

    //? if >=1.21.8 <=1.21.10 {
    /*@ModifyVariable(method = "forEachLine", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int chatting$fadeTicks(int tickCount) {
        return ChattingConfig.INSTANCE.getFade() ? tickCount + chatting$fadeOffset() : tickCount;
    }

    @ModifyVariable(method = "forEachLine", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean chatting$fadeFocused(boolean focused) {
        return focused || !ChattingConfig.INSTANCE.getFade();
    }
    *///?}

    //? if >=1.21.11 <26 {
    /*@ModifyArg(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;timeBased(I)Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;"), index = 0)
    private int chatting$fade(int tickCount) {
        if (!ChattingConfig.INSTANCE.getFade()) return tickCount - 1_000_000_000;
        return tickCount + chatting$fadeOffset();
    }
    *///?}

    //? if >=26 {
    @ModifyArg(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;timeBased(I)Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;"), index = 0)
    private int chatting$fade(int tickCount) {
        if (!ChattingConfig.INSTANCE.getFade()) return tickCount - 1_000_000_000;
        return tickCount + chatting$fadeOffset();
    }
    //?}

    //? if = 1.8.9 {
    /*@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(IIIII)V", ordinal = 1))
    private void chatting$scrollBar1(int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) GuiElement.fill(x1, y1, x2, y2, color);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(IIIII)V", ordinal = 2))
    private void chatting$scrollBar2(int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) GuiElement.fill(x1, y1, x2, y2, color);
    }
    *///?} elif <=1.21.5 {
    /*@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIIII)V"))
    private void chatting$scrollBar(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) graphics.fill(x1, y1, x2, y2, z, color);
    }
    *///?}

    //? if >=1.21.8 <=1.21.10 {
    /*@Redirect(method = {"render", "forEachLine"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 1))
    private void chatting$scrollBar1(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) graphics.fill(x1, y1, x2, y2, color);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 2))
    private void chatting$scrollBar2(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) graphics.fill(x1, y1, x2, y2, color);
    }
    *///?}

    //? if >=1.21.11 <26 {
    /*@Redirect(method = {"render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V", "forEachLine"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 1))
    private void chatting$scrollBar1(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 2))
    private void chatting$scrollBar2(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }
    *///?}

    //? if >=26 {
    @Redirect(method = {"extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", "forEachLine"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return chatting$previewing ? 0 : ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 2))
    private void chatting$scrollBar1(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 3))
    private void chatting$scrollBar2(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }
    //?}
}
