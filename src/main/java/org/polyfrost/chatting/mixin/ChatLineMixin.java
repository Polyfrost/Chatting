/*
 * This file is from chat_heads is licensed under MPL-2.0, which can be found at https://www.mozilla.org/en-US/MPL/2.0/
 * See: https://github.com/dzwdz/chat_heads/blob/fabric-1.16.x/LICENSE
 */

package org.polyfrost.chatting.mixin;

import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatLineHook;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.utils.ChatHeadHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Mixin(ChatLine.class)
public class ChatLineMixin implements ChatLineHook {
    private boolean detected = false;
    private boolean firstDetection = true;
    private NetworkPlayerInfo playerInfo;
    private NetworkPlayerInfo detectedPlayerInfo;
    private static long lastUniqueId = 0;
    private long uniqueId = 0;
    private long timestamp;
    private List<ChatLine> children = new ArrayList<>();

    @Override
    public List<ChatLine> getChildren() {
        return children;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int i, IChatComponent iChatComponent, int j, CallbackInfo ci) {
        lastUniqueId++;
        uniqueId = lastUniqueId;
        chatLines.add(new WeakReference<>((ChatLine) (Object) this));
        ChatHeadHooks.INSTANCE.detect(iChatComponent.getFormattedText(), (ChatLine) (Object) this);
    }

    @Override
    public boolean isDetected() {
        return detected;
    }

    @Override
    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    @Override
    public NetworkPlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    @Override
    public void setPlayerInfo(NetworkPlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    @Override
    public NetworkPlayerInfo getDetectedPlayerInfo() {
        return detectedPlayerInfo;
    }

    @Override
    public void setDetectedPlayerInfo(NetworkPlayerInfo detectedPlayerInfo) {
        this.detectedPlayerInfo = detectedPlayerInfo;
    }

    @Override
    public boolean isFirstDetection() {
        return firstDetection;
    }

    @Override
    public void setFirstDetection(boolean firstDetection) {
        this.firstDetection = firstDetection;
    }

    @Override
    public void updatePlayerInfo() {
        if (ChattingConfig.INSTANCE.getHideChatHeadOnConsecutiveMessages() && !firstDetection) {
            playerInfo = null;
        } else {
            playerInfo = detectedPlayerInfo;
        }
    }

    @Override
    public long getUniqueId() {
        return uniqueId;
    }
}
