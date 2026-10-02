/*
 * Player detection here is adapted from chat_heads, licensed MPL-2.0:
 * https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
 */
package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHeadState;
import org.polyfrost.chatting.hook.ChatLineHeadHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatLine.class)
public class ChatLineMixin_ChatHeads implements ChatLineHeadHook {
    @Unique private boolean chatting$detected;
    @Unique private boolean chatting$first = true;
    @Unique private NetworkPlayerInfo chatting$playerInfo;
    @Unique private NetworkPlayerInfo chatting$detectedPlayerInfo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void chatting$detectPlayer(int updateCounter, IChatComponent component, int chatLineId, CallbackInfo ci) {
        IChatComponent source = ChatHeadState.currentComponent != null ? ChatHeadState.currentComponent : component;
        NetworkPlayerInfo info = ChatHeadState.detect(source);
        if (info == null) return;

        chatting$detected = true;
        chatting$detectedPlayerInfo = info;
        chatting$playerInfo = info;
        if (ChatHeadState.lineVisible) {
            if (chatting$samePlayer(info, ChatHeadState.lastPlayerInfo)) {
                chatting$first = false;
                chatting$applyConsecutivePolicy();
            }
            ChatHeadState.lastPlayerInfo = info;
        }
    }

    @Unique
    private static boolean chatting$samePlayer(NetworkPlayerInfo first, NetworkPlayerInfo second) {
        return first != null && second != null && first.getGameProfile().getId().equals(second.getGameProfile().getId());
    }

    @Override
    public boolean chatting$hasDetectedPlayer() {
        return chatting$detected;
    }

    @Override
    public NetworkPlayerInfo chatting$getPlayerInfo() {
        return chatting$playerInfo;
    }

    @Unique
    private void chatting$applyConsecutivePolicy() {
        chatting$playerInfo = ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages() && !chatting$first
            ? null
            : chatting$detectedPlayerInfo;
    }
}
*///?}
