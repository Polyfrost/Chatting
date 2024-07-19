/*
 * This file is from chat_heads is licensed under MPL-2.0, which can be found at https://www.mozilla.org/en-US/MPL/2.0/
 * See: https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
 */

package org.polyfrost.chatting.mixin;

import org.apache.commons.lang3.StringUtils;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHook;
import org.polyfrost.chatting.hook.ChatLineHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;
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
public class ChatLineMixin implements ChatLineHook {
    @Unique
    private boolean chatting$detected = false;
    @Unique
    private boolean chatting$first = true;
    @Unique
    private NetworkPlayerInfo chatting$playerInfo;
    @Unique
    private NetworkPlayerInfo chatting$detectedPlayerInfo;
    @Unique
    private static NetworkPlayerInfo chatting$lastPlayerInfo;
    @Unique
    private static long chatting$lastUniqueId = 0;
    @Unique
    private long chatting$uniqueId = 0;
    @Unique
    private ChatLine chatting$fullMessage = null;
    @Unique
    private static ChatLine chatting$lastChatLine = null;
    @Unique
    private static final Pattern chatting$pattern = Pattern.compile("(§.)|\\W");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int i, IChatComponent iChatComponent, int chatId, CallbackInfo ci) {
        chatting$lastUniqueId++;
        chatting$uniqueId = chatting$lastUniqueId;
        if (chatting$lastChatLine == ChatHook.currentLine) {
            if (chatting$lastPlayerInfo != null) {
                return;
            }
        }
        chatting$fullMessage = ChatHook.currentLine;
        chatting$lastChatLine = chatting$fullMessage;
        chatting$chatLines.add(new WeakReference<>((ChatLine) (Object) this));
        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return;
        Map<String, NetworkPlayerInfo> nicknameCache = new HashMap<>();
        try {
            for (String word : chatting$pattern.split(StringUtils.substringBefore(iChatComponent.getFormattedText(), ":"))) {
                if (word.isEmpty()) continue;
                chatting$playerInfo = netHandler.getPlayerInfo(word);
                if (chatting$playerInfo == null) {
                    chatting$playerInfo = chatting$getPlayerFromNickname(word, netHandler, nicknameCache);
                }
                if (chatting$playerInfo != null) {
                    chatting$detectedPlayerInfo = chatting$playerInfo;
                    chatting$detected = true;
                    if (ChatHook.lineVisible) {
                        if (chatting$lastPlayerInfo != null && chatting$playerInfo.getGameProfile() == chatting$lastPlayerInfo.getGameProfile()) {
                            chatting$first = false;
                            if (ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages()) {
                                chatting$playerInfo = null;
                            }
                        }
                        chatting$lastPlayerInfo = chatting$detectedPlayerInfo;
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    @Nullable
    private static NetworkPlayerInfo chatting$getPlayerFromNickname(String word, NetHandlerPlayClient connection, Map<String, NetworkPlayerInfo> nicknameCache) {
        if (nicknameCache.isEmpty()) {
            for (NetworkPlayerInfo p : connection.getPlayerInfoMap()) {
                IChatComponent displayName = p.getDisplayName();
                if (displayName != null) {
                    String nickname = displayName.getUnformattedTextForChat();
                    if (word.equals(nickname)) {
                        nicknameCache.clear();
                        return p;
                    }

                    nicknameCache.put(nickname, p);
                }
            }
        } else {
            // use prepared cache
            return nicknameCache.get(word);
        }

        return null;
    }

    @Override
    public boolean chatting$hasDetected() {
        return chatting$detected;
    }

    @Override
    public NetworkPlayerInfo chatting$getPlayerInfo() {
        return chatting$playerInfo;
    }

    @Override
    public void chatting$updatePlayerInfo() {
        if (ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages() && !chatting$first) {
            chatting$playerInfo = null;
        } else {
            chatting$playerInfo = chatting$detectedPlayerInfo;
        }
    }

    @Override
    public long chatting$getUniqueId() {
        return chatting$uniqueId;
    }

    @Override
    public ChatLine chatting$getFullMessage() {
        return chatting$fullMessage;
    }
}
