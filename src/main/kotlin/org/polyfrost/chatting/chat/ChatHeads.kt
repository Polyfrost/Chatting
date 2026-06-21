package org.polyfrost.chatting.chat

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.util.FormattedCharSequence
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.Collections
import java.util.WeakHashMap

object ChatHeads {

    private val SPLIT = Regex("\\W")

    private val headLines: MutableMap<FormattedCharSequence, PlayerInfo> =
        Collections.synchronizedMap(WeakHashMap())

    private val hiddenHeads: MutableSet<FormattedCharSequence> =
        Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap()))

    fun tag(content: FormattedCharSequence, info: PlayerInfo?, hidden: Boolean) {
        if (info == null) {
            headLines.remove(content)
            hiddenHeads.remove(content)
        } else {
            headLines[content] = info
            if (hidden) hiddenHeads.add(content) else hiddenHeads.remove(content)
        }
    }

    fun lookup(content: FormattedCharSequence): PlayerInfo? = headLines[content]

    fun shouldDrawHead(info: PlayerInfo?, hidden: Boolean): Boolean = info != null && !hidden

    fun shouldOffset(info: PlayerInfo?): Boolean =
        info != null || ChattingConfig.offsetNonPlayerMessages

    fun sameOwner(a: PlayerInfo?, b: PlayerInfo?): Boolean =
        a != null && b != null && a.profile.id == b.profile.id

    fun isHidden(content: FormattedCharSequence): Boolean = hiddenHeads.contains(content)

    fun detect(message: String): PlayerInfo? {
        val connection = mc.connection ?: return null
        val before = message.substringBefore(":")
        val words = SPLIT.split(before).filter { it.isNotEmpty() }
        if (words.isEmpty()) return null

        for (word in words) {
            connection.getPlayerInfo(word)?.let { return it }
        }

        for (player in connection.onlinePlayers) {
            val displayName = player.tabListDisplayName?.string ?: continue
            if (words.any { it == displayName }) return player
        }
        return null
    }
}
