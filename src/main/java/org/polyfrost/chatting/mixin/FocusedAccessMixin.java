package org.polyfrost.chatting.mixin;

//? if > 1.8.9 {
//? if >=1.21.11 {
import org.polyfrost.chatting.chat.ChatBackground;
import org.polyfrost.chatting.chat.ChatHover;
import org.polyfrost.chatting.chat.RoundedChat;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hud.ChatWindowHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
//? if <26 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess")
public class FocusedAccessMixin {

    @Unique private int chatting$mouseX;
    @Unique private int chatting$mouseY;
    // a fresh DrawingFocusedGraphicsAccess is constructed per render pass so no reset is needed
    @Unique private boolean chatting$sawLineFill;

    @Inject(method = "<init>", at = @At("TAIL"))
    //~ if <26 'GuiGraphicsExtractor' -> 'GuiGraphics'
    private void chatting$captureMouse(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, boolean changeCursor, CallbackInfo ci) {
        this.chatting$mouseX = mouseX;
        this.chatting$mouseY = mouseY;
    }

    //~ if <26 'GuiGraphicsExtractor' -> 'GuiGraphics'
    @Redirect(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    //~ if <26 'GuiGraphicsExtractor' -> 'GuiGraphics'
    private void chatting$hoverFill(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        int chatting$ex2 = chatting$chatFocused() ? x2 + org.polyfrost.chatting.chat.ChatButtons.extraBackgroundWidth() : x2;
        // line backgrounds are the only fills through this method with x1 == -4
        boolean chatting$lineFill = x1 == -4;
        int chatting$chatBottom = RoundedChat.chatBottom(graphics.guiHeight());
        int chatting$c = chatting$lineFill ? chatting$lineColor(y2, color, chatting$chatBottom) : color;
        boolean chatting$top = chatting$lineFill && !chatting$sawLineFill;
        boolean chatting$bottom = chatting$lineFill && y2 == chatting$chatBottom;
        if (chatting$lineFill) chatting$sawLineFill = true;
        RoundedChat.fill(graphics::fill, RoundedChat.scaler(graphics.pose()),
            x1, y1, chatting$ex2, y2, chatting$c, chatting$top, chatting$bottom);
    }

    @Unique
    private int chatting$lineColor(int y2, int color, int chatBottom) {
        //? if >=26.2 {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        //?} else
        //ChatComponent chat = Minecraft.getInstance().gui.getChat();
        int line = (chatBottom - y2) / ((ChatComponentAccessor) chat).chatting$getLineHeight() + ChatHover.scrollPos();
        // truncated to match the buttons
        int mouseX = (int) ChatWindowHud.mapMouseX(chatting$mouseX);
        int mouseY = (int) ChatWindowHud.mapMouseY(chatting$mouseY);
        if (ChatHover.highlighted(chat, mouseX, mouseY, line)) {
            return ChattingConfig.INSTANCE.getHoveredChatBackgroundColor().getArgb();
        }
        return ChatBackground.tint(color);
    }

    @Unique
    private boolean chatting$chatFocused() {
        //? if >=26.2 {
        return Minecraft.getInstance().gui.screen() instanceof net.minecraft.client.gui.screens.ChatScreen;
        //?} else {
        /*return Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.ChatScreen;
        *///?}
    }
}
//?}
//? if <1.21.11 {
/*public class FocusedAccessMixin {
}
*///?}
//?}
