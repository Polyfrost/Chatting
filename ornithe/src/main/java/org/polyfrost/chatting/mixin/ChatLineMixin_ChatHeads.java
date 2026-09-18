/*
 * Player detection here is adapted from chat_heads, licensed MPL-2.0:
 * https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
 */
package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.EnumChatFormatting;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHeadState;
import org.polyfrost.chatting.hook.ChatLineHeadHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Mixin(ChatLine.class)
public class ChatLineMixin_ChatHeads implements ChatLineHeadHook {
    @Unique private static final Pattern chatting$separator = Pattern.compile("(§.)|\\W");
    @Unique private boolean chatting$detected;
    @Unique private boolean chatting$first = true;
    @Unique private NetworkPlayerInfo chatting$playerInfo;
    @Unique private NetworkPlayerInfo chatting$detectedPlayerInfo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void chatting$detectPlayer(int updateCounter, IChatComponent component, int chatLineId, CallbackInfo ci) {
        ChatLineHeadHook.LINES.add(new WeakReference<>((ChatLine) (Object) this));
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getNetHandler();
        if (connection == null) return;

        IChatComponent source = ChatHeadState.currentComponent != null ? ChatHeadState.currentComponent : component;
        String formatted = source.getFormattedText();
        int colon = formatted.indexOf(':');
        String prefix = EnumChatFormatting.getTextWithoutFormattingCodes(colon >= 0 ? formatted.substring(0, colon) : formatted);
        Map<String, NetworkPlayerInfo> nicknames = new HashMap<>();

        for (String word : chatting$separator.split(prefix)) {
            if (word.isEmpty()) continue;
            NetworkPlayerInfo info = connection.getPlayerInfo(word);
            if (info == null) info = chatting$fromNickname(word, connection, nicknames);
            if (info == null) continue;

            chatting$detected = true;
            chatting$detectedPlayerInfo = info;
            chatting$playerInfo = info;
            if (ChatHeadState.lineVisible) {
                if (chatting$samePlayer(info, ChatHeadState.lastPlayerInfo)) {
                    chatting$first = false;
                    chatting$updatePlayerInfo();
                }
                ChatHeadState.lastPlayerInfo = info;
            }
            return;
        }
    }

    @Unique
    private static boolean chatting$samePlayer(NetworkPlayerInfo first, NetworkPlayerInfo second) {
        return first != null && second != null && first.getGameProfile().getId().equals(second.getGameProfile().getId());
    }

    @Unique
    private static NetworkPlayerInfo chatting$fromNickname(String word, NetHandlerPlayClient connection, Map<String, NetworkPlayerInfo> nicknames) {
        if (nicknames.isEmpty()) {
            for (NetworkPlayerInfo info : connection.getPlayerInfoMap()) {
                IChatComponent displayName = info.getDisplayName();
                if (displayName == null) continue;
                String nickname = EnumChatFormatting.getTextWithoutFormattingCodes(displayName.getFormattedText());
                if (word.equals(nickname)) return info;
                nicknames.put(nickname, info);
            }
        }
        return nicknames.get(word);
    }

    @Override
    public boolean chatting$hasDetectedPlayer() {
        return chatting$detected;
    }

    @Override
    public NetworkPlayerInfo chatting$getPlayerInfo() {
        return chatting$playerInfo;
    }

    @Override
    public void chatting$updatePlayerInfo() {
        chatting$playerInfo = ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages() && !chatting$first
            ? null
            : chatting$detectedPlayerInfo;
    }
}
