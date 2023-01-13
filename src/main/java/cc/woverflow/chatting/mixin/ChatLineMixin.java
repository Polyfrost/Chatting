/*
 * This file is from chat_heads is licensed under MPL-2.0, which can be found at https://www.mozilla.org/en-US/MPL/2.0/
 * See: https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
 */

package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.chatting.hook.ChatLineHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@Mixin(ChatLine.class)
public class ChatLineMixin implements ChatLineHook {
    private boolean detected = false;
    private boolean first = true;
    private NetworkPlayerInfo playerInfo;
    private NetworkPlayerInfo detectedPlayerInfo;
    private static NetworkPlayerInfo lastPlayerInfo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int i, IChatComponent iChatComponent, int j, CallbackInfo ci) {
        chatLines.add(new WeakReference<>((ChatLine) (Object) this));
        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return;
        Map<String, NetworkPlayerInfo> nicknameCache = new HashMap<>();
        try {
            for (String word : iChatComponent.getFormattedText().split("(§.)|\\W")) {
                if (word.isEmpty()) continue;
                playerInfo = netHandler.getPlayerInfo(word);
                if (playerInfo == null) {
                    playerInfo = getPlayerFromNickname(word, netHandler, nicknameCache);
                }
                if (playerInfo != null) {
                    detectedPlayerInfo = playerInfo;
                    detected = true;
                    if (playerInfo == lastPlayerInfo) {
                        first = false;
                        if (ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages()) {
                            playerInfo = null;
                        }
                    } else {
                        lastPlayerInfo = playerInfo;
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Nullable
    private static NetworkPlayerInfo getPlayerFromNickname(String word, NetHandlerPlayClient connection, Map<String, NetworkPlayerInfo> nicknameCache) {
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
    public boolean hasDetected() {
        return detected;
    }

    @Override
    public NetworkPlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    @Override
    public void updatePlayerInfo() {
        if (ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages() && !first) {
            playerInfo = null;
        } else {
            playerInfo = detectedPlayerInfo;
        }
    }
}
