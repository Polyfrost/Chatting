package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.libs.universal.UChat
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.polyfrost.chatting.config.ChattingConfig
import java.util.regex.Matcher
import java.util.regex.Pattern

object ChatMention {
    private val MESSAGE: Pattern = Pattern.compile("(.+): (.*)$")
    private val prevColor = Regex("(§[a-zA-Z])(?!.*§[a-zA-Z])")
    private val PLAYER_NAME: String = Minecraft.getMinecraft().session.username

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onChat(event: ClientChatReceivedEvent) {
        val chat = event.message.formattedText
        println(chat)
        val matcher: Matcher = MESSAGE.matcher(chat)
        if (matcher.matches()) {
            if (!matcher.group(1).contains(PLAYER_NAME)) {
                if (matcher.group(2).contains(PLAYER_NAME) || matcher.group(2).contains(PLAYER_NAME.lowercase())) {
                    if (ChattingConfig.pingName) Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1F, 1F)
                    if (ChattingConfig.highlightName) {
                        val originalColor = prevColor.find(matcher.group(2))?.value ?: ""
                        val replacement = "§e$PLAYER_NAME$originalColor"
                        UChat.chat(event.message.formattedText.replace(PLAYER_NAME, replacement))
                        event.isCanceled = true
                    }
                }
            }
        }
    }
}