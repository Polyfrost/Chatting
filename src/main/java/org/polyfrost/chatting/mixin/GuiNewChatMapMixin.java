package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hook.GuiNewChatHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.twitch.chat.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = GuiNewChat.class, priority = Integer.MIN_VALUE)
public abstract class GuiNewChatMapMixin implements GuiNewChatHook {

    @Unique private final Map<ChatLine, ChatLine> drawnToFull = new HashMap<>();
    @Unique private final List<ChatLine> tempDrawnLines = new ArrayList<>();
    @Unique private ChatLine lastTempLine = null;

    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Shadow @Final private List<ChatLine> chatLines;

    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void handleSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        tempDrawnLines.clear();
    }

    @Inject(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER, remap = false))
    private void handleDrawnLineAdded(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (!displayOnly) tempDrawnLines.add(drawnChatLines.get(0));
        else if (lastTempLine != null) {
            drawnToFull.put(drawnChatLines.get(0), lastTempLine);
        }
    }

    @Inject(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;", ordinal = 0, shift = At.Shift.BEFORE, remap = false))
    private void handleDrawnLineRemoved(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        drawnToFull.remove(drawnChatLines.get(drawnChatLines.size() - 1));
    }

    @Inject(method = "setChatLine", at = @At("RETURN"))
    private void handleLineAdded(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (!displayOnly) {
            ChatLine masterLine = chatLines.get(0);
            ChatLineHook masterHook = (ChatLineHook) masterLine;
            masterHook.setTimestamp(System.currentTimeMillis());
            masterHook.getChildren().addAll(tempDrawnLines);
            for (ChatLine tempDrawnLine : tempDrawnLines) drawnToFull.put(tempDrawnLine, masterLine);
        }else {
            lastTempLine = null;
        }
    }

    @Inject(method = "refreshChat", at = @At("HEAD"))
    private void handleRefreshedChat(CallbackInfo ci) {
        drawnToFull.clear();
    }

    @Inject(method = "refreshChat", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/IChatComponent;IIZ)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void handleLineRefresh(CallbackInfo ci, int i, ChatLine chatline) {
        lastTempLine = chatline;
    }

    @Inject(method = "clearChatMessages", at = @At("HEAD"))
    private void handleChatCleared(CallbackInfo ci) {
        drawnToFull.clear();
    }

    @Override
    public ChatLine getFullMessage(ChatLine line) {
        return drawnToFull.getOrDefault(line, null);
    }
}
