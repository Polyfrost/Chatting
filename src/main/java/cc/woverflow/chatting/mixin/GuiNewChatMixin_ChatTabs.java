package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.chat.ChatTabs;
import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.chatting.utils.ModCompatHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        handleChatTabMessage(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false, ci);
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    private void handleSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        handleChatTabMessage(chatComponent, chatLineId, updateCounter, displayOnly, ci);
    }

    private void handleChatTabMessage(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            if (!ChatTabs.INSTANCE.shouldRender(chatComponent)) {
                ChatTabs.INSTANCE.setHasCancelledAnimation(true);
                if (chatLineId != 0) {
                    deleteChatLine(chatLineId);
                }
                if (!displayOnly) {
                    chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
                    while (this.chatLines.size() > (100 + ModCompatHooks.getExtendedChatLength())) {
                        this.chatLines.remove(this.chatLines.size() - 1);
                    }
                }
                ci.cancel();
            }
        }
    }
}
