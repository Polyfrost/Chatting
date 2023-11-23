package org.polyfrost.chatting.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.network.NetworkPlayerInfo
import org.polyfrost.chatting.config.ChattingConfig.hideChatHeadOnConsecutiveMessages
import org.polyfrost.chatting.hook.ChatLineHook

object ChatHeadHooks {
    private var lastPlayerInfo: NetworkPlayerInfo? = null
    fun detect(formattedText: String, chatLine: ChatLine?): Boolean {
        if (chatLine !is ChatLineHook) {
            return false
        }
        val netHandler = Minecraft.getMinecraft().netHandler ?: return false
        val nicknameCache: MutableMap<String, NetworkPlayerInfo> = HashMap()
        var detected = false
        try {
            formattedText.split("(§.)|\\W".toRegex()).dropLastWhile { it.isEmpty() }
                .forEach { word ->
                    if (word.isNotEmpty()) {
                        var maybePlayerInfo = netHandler.getPlayerInfo(word)
                        if (maybePlayerInfo == null) {
                            maybePlayerInfo = getPlayerFromNickname(word, netHandler, nicknameCache)
                        }
                        if (maybePlayerInfo != null) {
                            detected = true
                            chatLine.run {
                                playerInfo = maybePlayerInfo
                                detectedPlayerInfo = playerInfo
                                isDetected = true
                                if (playerInfo == lastPlayerInfo) {
                                    isFirstDetection = false
                                    if (hideChatHeadOnConsecutiveMessages) {
                                        playerInfo = null
                                    }
                                } else {
                                    lastPlayerInfo = playerInfo
                                }
                                return@forEach
                            }
                        }
                    }
                }
        } catch (ignored: Exception) {
        }
        return detected
    }

    private fun getPlayerFromNickname(
        word: String,
        connection: NetHandlerPlayClient,
        nicknameCache: MutableMap<String, NetworkPlayerInfo>
    ): NetworkPlayerInfo? {
        if (nicknameCache.isEmpty()) {
            for (p in connection.playerInfoMap) {
                val displayName = p.displayName
                if (displayName != null) {
                    val nickname = displayName.unformattedTextForChat
                    if (word == nickname) {
                        nicknameCache.clear()
                        return p
                    }
                    nicknameCache[nickname] = p
                }
            }
        } else {
            // use prepared cache
            return nicknameCache[word]
        }
        return null
    }
}