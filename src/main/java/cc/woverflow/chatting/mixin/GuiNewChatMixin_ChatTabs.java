package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.chat.ChatSearchingManager;
import cc.woverflow.chatting.chat.ChatTabs;
import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.chatting.utils.ModCompatHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_ChatTabs {
    @Shadow @Final private Minecraft mc;

    @Shadow public abstract void deleteChatLine(int id);

    @Shadow @Final private List<ChatLine> chatLines;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private float percentComplete; //betterchat support

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"), cancellable = true)
    private void handlePrintChatMessage(IChatComponent chatComponent, int chatLineId, CallbackInfo ci) {
        handleChatTabMessage(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false, ci);
        if (!EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText()).toLowerCase(Locale.ENGLISH).contains(ChatSearchingManager.INSTANCE.getLastSearch().toLowerCase(Locale.ENGLISH))) {
            percentComplete = 1.0F;
        }
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    private void handleSetChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        handleChatTabMessage(chatComponent, chatLineId, updateCounter, displayOnly, ci);
    }

    private void handleChatTabMessage(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            if (!ChatTabs.INSTANCE.shouldRender(chatComponent)) {
                percentComplete = 1.0F;
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
