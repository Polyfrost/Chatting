package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.hook.HeadHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if >=26 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
*///?} else {
import net.minecraft.client.GuiMessage;
//?}
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.polyfrost.chatting.chat.ChatButtons;
import org.polyfrost.chatting.chat.ChatHeads;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.chat.ChatSearch;
import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.chat.SmoothChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Shadow;
import org.polyfrost.chatting.hook.ChatComponentHook;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hud.ChatWindowHud;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import net.minecraft.client.gui.Font;
//? if <= 1.21.11
import net.minecraft.client.gui.components.PlayerFaceRenderer;
//? if <=1.21.10 {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
//?}
//? if <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}

@Mixin(ChatComponent.class)
public class ChatComponentMixin implements ChatComponentHook {
    //? if <= 1.21.11 {
    @SuppressWarnings("InstantiationOfUtilityClass")
    @Unique PlayerFaceRenderer chatting$playerFaceRenderer = new PlayerFaceRenderer();
    //?}

    @Shadow
    private void refreshTrimmedMessages() {
        throw new AssertionError();
    }

    @Override
    public void chatting$refresh() {
        refreshTrimmedMessages();
    }

    //? if <=1.21.10 {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean chatting$peek(boolean focused) {
        return focused || Chatting.INSTANCE.getPeeking();
    }
    //?} elif <26 {
    /*@ModifyVariable(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean chatting$peek(boolean focused) {
        return focused || Chatting.INSTANCE.getPeeking();
    }
    *///?} else {
    /*@ModifyVariable(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private ChatComponent.DisplayMode chatting$peek(ChatComponent.DisplayMode mode) {
        return (mode == ChatComponent.DisplayMode.BACKGROUND && Chatting.INSTANCE.getPeeking())
            ? ChatComponent.DisplayMode.FOREGROUND
            : mode;
    }
    *///?}

    //? if >=1.21.11 <26 {
    /*@Unique private boolean chatting$posed;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"))
    private void chatting$beginChatWindow(GuiGraphics graphics, Font font, int ticks, int mouseX, int mouseY, boolean focused, boolean changeCursor, CallbackInfo ci) {
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
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
        if (!chatting$posed) return;
        chatting$posed = false;
        graphics.pose().popMatrix();
    }
    *///?} elif >=26 {
    /*@Unique private boolean chatting$posed;

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"))
    private void chatting$beginChatWindow(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode mode, boolean changeCursor, CallbackInfo ci) {
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
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
        if (!chatting$posed) return;
        chatting$posed = false;
        graphics.pose().popMatrix();
    }
    *///?}

    @Unique
    private PlayerInfo chatting$pendingHead;
    @Unique
    private boolean chatting$pendingHideHead;
    @Unique
    private boolean chatting$headConsumed;
    @Unique
    private PlayerInfo chatting$lastHeadOwner;

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
        chatting$headConsumed = false;
        chatting$pendingHead = ChattingConfig.INSTANCE.getShowChatHeads()
            ? ChatHeads.INSTANCE.detect(guiMessage.content().getString())
            : null;
        chatting$pendingHideHead = ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages()
            && ChatHeads.INSTANCE.sameOwner(chatting$pendingHead, chatting$lastHeadOwner);
        chatting$lastHeadOwner = chatting$pendingHead;
        if (!chatting$refreshing) SmoothChat.INSTANCE.start();
    }

    @Unique
    private void chatting$applyHead(Object element) {
        if (!chatting$refreshing) SmoothChat.INSTANCE.addLine(((GuiMessage.Line) element).content());
        if (chatting$headConsumed) return;
        chatting$headConsumed = true;
        ((ChatLineHook) element).chatting$setPlayerInfo(chatting$pendingHead);
        ((ChatLineHook) element).chatting$setHeadHidden(chatting$pendingHideHead);
        ChatHeads.INSTANCE.tag(((GuiMessage.Line) element).content(), chatting$pendingHead, chatting$pendingHideHead);
    }

    @Unique
    private boolean chatting$refreshing;

    @Shadow
    private int chatScrollbarPos;

    @Inject(method = "scrollChat", at = @At("HEAD"))
    private void chatting$armSmoothScroll(int amount, CallbackInfo ci) {
        ChatScrolling.INSTANCE.setShouldSmooth(true);
    }

    @Inject(method = "refreshTrimmedMessages", at = @At("HEAD"))
    private void chatting$beginRefresh(CallbackInfo ci) {
        chatting$refreshing = true;
        chatting$lastHeadOwner = null;
    }

    @Inject(method = "refreshTrimmedMessages", at = @At("RETURN"))
    private void chatting$endRefresh(CallbackInfo ci) {
        chatting$refreshing = false;
    }

    //? if <=1.21.10 {
    @Redirect(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V"))
    private void chatting$tagHead(List<Object> list, int index, Object element) {
        chatting$applyHead(element);
        list.add(index, element);
    }
    //?} else {
    /*@Redirect(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V"))
    private void chatting$tagHead(List<Object> list, Object element) {
        chatting$applyHead(element);
        list.addFirst(element);
    }
    *///?}

    //? if <=1.21.10 {
    @Unique
    private boolean chatting$posed;

    @Inject(method = "render", at = @At("HEAD"))
    private void chatting$beginChatWindow(GuiGraphics graphics, int tick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        ChatScrolling.INSTANCE.step(chatScrollbarPos);
        boolean hud = ChatWindowHud.isActive();
        float smoothDy = SmoothChat.INSTANCE.translateY(chatScrollbarPos > 0);
        chatting$posed = hud || smoothDy != 0f;
        if (!chatting$posed) return;
        float scale = ChatWindowHud.chatScale();
        //? if <1.21.6 {
        graphics.pose().pushPose();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy, 0.0F);
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY(), 0.0F);
            if (scale != 1f) graphics.pose().scale(scale, scale, 1.0F);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop(), 0.0F);
        }
        //?} else {
        /*graphics.pose().pushMatrix();
        if (smoothDy != 0f) graphics.pose().translate(0.0F, smoothDy);
        if (hud) {
            graphics.pose().translate(ChatWindowHud.chatTranslateX(), ChatWindowHud.chatTranslateY());
            if (scale != 1f) graphics.pose().scale(scale, scale);
            graphics.pose().translate(-ChatWindowHud.anchorLeft(), -ChatWindowHud.anchorTop());
        }
        *///?}
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void chatting$endChatWindow(GuiGraphics graphics, int tick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (!chatting$posed) return;
        chatting$posed = false;
        //? if <1.21.6 {
        graphics.pose().popPose();
        //?} else {
        /*graphics.pose().popMatrix();
        *///?}
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int chatting$renderMouseX(int mouseX) {
        return (int) ChatWindowHud.mapMouseX(mouseX);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private int chatting$renderMouseY(int mouseY) {
        return (int) ChatWindowHud.mapMouseY(mouseY);
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
    //?}

    //? if <=1.21.10 {
    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Unique
    private int chatting$hovered = -2;

    @Unique
    private int chatting$drawHead(GuiGraphics graphics, GuiMessage.Line line, int x, int y, int alpha) {
        if (!ChattingConfig.INSTANCE.getShowChatHeads()) return x;
        PlayerInfo info = ((ChatLineHook) (Object) line).chatting$getPlayerInfo();
        boolean hidden = ((ChatLineHook) (Object) line).chatting$isHeadHidden();
        if (ChatHeads.INSTANCE.shouldDrawHead(info, hidden)) {
            //? if <1.21.4 {
            graphics.setColor(1f, 1f, 1f, alpha / 255f);
            if (ChattingConfig.INSTANCE.getImprovedHeads()) ((HeadHook) chatting$playerFaceRenderer).chatting$draw(graphics, info.getSkin().texture(), x, y - 1, 8, -1, true, false);
            else PlayerFaceRenderer.draw(graphics, info.getSkin(), x, y - 1, 8);
            graphics.setColor(1f, 1f, 1f, 1f);
            //?} else {
            /*if (ChattingConfig.INSTANCE.getImprovedHeads()) ((HeadHook) chatting$playerFaceRenderer).chatting$draw(graphics, info.getSkin()/^? if >= 1.21.10 {^//^.body().texturePath()^//^?} else {^/.texture()/^?}^/, x, y - 1, 0xFFFFFF | (alpha << 24), 8, true, false);
            else PlayerFaceRenderer.draw(graphics, info.getSkin(), x, y - 1, 8, 0xFFFFFF | (alpha << 24));
            *///?}
        }
        return ChatHeads.INSTANCE.shouldOffset(info) ? x + 10 : x;
    }

    @Unique
    private void chatting$drawHoverBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, GuiMessage.Line line) {
        if (((ChatComponent) (Object) this).isChatFocused()) {
            x2 += ChatButtons.extraBackgroundWidth();
        }
        if (chatting$hovered >= 0 && trimmedMessages.indexOf(line) == chatting$hovered) {
            graphics.fill(x1, y1, x2, y2, ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb());
        } else {
            graphics.fill(x1, y1, x2, y2, color);
        }
    }

    @ModifyExpressionValue(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;getMessageEndIndexAt(DD)I")
    )
    private int chatting$captureHover(int hovered) {
        chatting$hovered = hovered;
        return hovered;
    }

    //? if <=1.21.5 {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
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
    //?} else {
    
    /*// method_71991 = line text (drawString), method_71992 = line background (fill)
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
    *///?}
    //?}

    @Unique
    private int chatting$fadeOffset() {
        return 200 - (int) (ChattingConfig.INSTANCE.getFadeTime() * 20);
    }

    //? if <=1.21.5 {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;addedTime()I"))
    private int chatting$fadeAge(int addedTime) {
        if (!ChattingConfig.INSTANCE.getFade()) return Integer.MAX_VALUE;
        return addedTime - chatting$fadeOffset();
    }
    //?}

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
    /*@ModifyArg(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;timeBased(I)Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;"), index = 0)
    private int chatting$fade(int tickCount) {
        if (!ChattingConfig.INSTANCE.getFade()) return tickCount - 1_000_000_000;
        return tickCount + chatting$fadeOffset();
    }
    *///?}

    //? if <=1.21.5 {
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIIII)V"))
    private void chatting$scrollBar(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) graphics.fill(x1, y1, x2, y2, z, color);
    }
    //?}

    //? if >=1.21.8 <=1.21.10 {
    /*@Redirect(method = {"render", "forEachLine"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return ChatScrolling.INSTANCE.pos();
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
        return ChatScrolling.INSTANCE.pos();
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
    /*@Redirect(method = {"extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", "forEachLine"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int chatting$smoothScrollPos(ChatComponent instance) {
        return ChatScrolling.INSTANCE.pos();
    }

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 2))
    private void chatting$scrollBar1(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V", ordinal = 3))
    private void chatting$scrollBar2(ChatComponent.ChatGraphicsAccess access, int x1, int y1, int x2, int y2, int color) {
        if (!ChattingConfig.INSTANCE.getRemoveScrollBar()) access.fill(x1, y1, x2, y2, color);
    }
    *///?}
}
