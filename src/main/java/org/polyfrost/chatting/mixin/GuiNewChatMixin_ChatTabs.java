package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.chat.ChatTabs;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.utils.ModCompatHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiNewChat.class, priority = 990)
public abstract class GuiNewChatMixin_ChatTabs {
    @Shadow @Final private Minecraft mc;

    @Shadow public abstract void deleteChatLine(int id);

    @Shadow @Final private List<ChatLine> chatLines;

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"), cancellable = true)
    private void handlePrintChatMessage(IChatComponent chatComponent, int chatLineId, CallbackInfo ci) {
        chatting$handleChatTabMessage(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false, ci);
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    private void handleSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        chatting$handleChatTabMessage(chatComponent, chatLineId, updateCounter, displayOnly, ci);
    }

    @Unique
    private void chatting$handleChatTabMessage(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            ChatTabs.INSTANCE.setHasCancelledAnimation(!ChatTabs.INSTANCE.shouldRender(chatComponent));
            if (!ChatTabs.INSTANCE.shouldRender(chatComponent)) {
                if (chatLineId != 0) {
                    deleteChatLine(chatLineId);
                }
                if (!displayOnly) {
                    this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
                    while (this.chatLines.size() > (100 + ModCompatHooks.getExtendedChatLength())) {
                        this.chatLines.remove(this.chatLines.size() - 1);
                    }
                }
                ci.cancel();
            }
        }
    }
}
