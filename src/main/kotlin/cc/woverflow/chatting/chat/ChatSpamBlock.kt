package cc.woverflow.chatting.chat

import cc.woverflow.chatting.config.ChattingConfig
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.text.Normalizer

object ChatSpamBlock {
    /*
    Made by @KTibow
    Based off of Unspam (also by @KTibow)
    Algorithm based off of https://paulgraham.com/spam.html
    */
    private val PLAYER_MESSAGE = Regex("^(\\[VIP\\+?\\] |\\[MVP\\+?\\+?\\] |)(\\w{2,16}): (.*)$")

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText.replace(Regex("\u00A7."), "")
        if (!PLAYER_MESSAGE.matches(message)) return
        val (rank, player, content) = PLAYER_MESSAGE.matchEntire(message)!!.destructured
        val tokens = tokenize(content)
        val spamProb = findSpamProbability(tokens)
        println("\n[CHATTING]$message")
        if (spamProb * 100 > ChattingConfig.spamThreshold) {
            if (ChattingConfig.showSpamInGray) {
                var newMessage = EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.STRIKETHROUGH.toString()
                if (!ChattingConfig.customFormatting) {
                    newMessage += rank
                }
                newMessage += "$player${EnumChatFormatting.DARK_GRAY.toString()}: $content"
                event.message = ChatComponentText(newMessage)
            } else {
                event.isCanceled = true
            }
            return
        }
        if (ChattingConfig.customFormatting) {
            val coloredPlayer = findRankColor(rank) + player + EnumChatFormatting.RESET.toString()
            event.message = ChatComponentText("$coloredPlayer: $content")
        }
    }

    private fun tokenize(message: String): MutableList<String> {
        val strippedMessage =
            Normalizer.normalize(message, Normalizer.Form.NFKC).replace(Regex("[^\\w\\s/]"), " ").lowercase().trim()
        val tokens = strippedMessage.split(Regex("\\s+")).toMutableList()
        if (tokens.size <= 2) {
            tokens.add("TINY_LENGTH")
        } else if (tokens.size <= 4) {
            tokens.add("SMALL_LENGTH")
        } else if (tokens.size <= 7) {
            tokens.add("MEDIUM_LENGTH")
        } else {
            tokens.add("LONG_LENGTH")
        }
        if (message.replace(Regex("[\\w\\s]"), "").length > 2) {
            tokens.add("SPECIAL_CHARS")
        } else if (message.replace(Regex("[\\w\\s]"), "").length > 0) {
            tokens.add("SPECIAL_CHAR")
        } else {
            tokens.add("LOW_SPECIAL_CHARS")
        }
        if (message.replace(Regex("[^A-Z]"), "").length >= message.length / 4) {
            tokens.add("HIGH_CAPS")
        } else {
            tokens.add("LOW_CAPS")
        }
        return tokens
    }

    private fun findSpamProbability(tokens: MutableList<String>): Double {
        val tokenProbs = mutableMapOf<String, Double>()
        for (token in tokens) {
            if (!spamInfoJson.has(token)) continue
            val spamInToken = spamInfoJson.get(token).asJsonObject.get("spam").asDouble
            val fineInToken = spamInfoJson.get(token).asJsonObject.get("fine").asDouble
            tokenProbs[token] = (
                (spamInToken / messageCountsJson.get("spam").asInt) /
                    (fineInToken / messageCountsJson.get("fine").asInt +
                    spamInToken / messageCountsJson.get("spam").asInt)
                )
        }
        val spamProbs = tokenProbs.values.toMutableList()
        val fineProbs = tokenProbs.values.map {
            1 - it
        }.toMutableList()
        val spamProbability = spamProbs.reduce { a, b ->
            a * b
        }
        val fineProbability = fineProbs.reduce { a, b ->
            a * b
        }
        return spamProbability / (spamProbability + fineProbability)
    }

    private fun findRankColor(rank: String): String {
        println(rank)
        return when (rank) {
            "[VIP] ", "[VIP+] " -> EnumChatFormatting.GREEN.toString()
            "[MVP] ", "[MVP+] " -> EnumChatFormatting.AQUA.toString()
            "[MVP++] " -> EnumChatFormatting.GOLD.toString()
            else -> EnumChatFormatting.GRAY.toString()
        }
    }

    private fun getResourceAsText(path: String): String? = object {}.javaClass.getResource(path)?.readText()
    private val spamInfoJson: JsonObject
    private val messageCountsJson: JsonObject

    init {
        // Load the file spamInfo.json from resources/
        val spamInfo = getResourceAsText("/spamInfo.json")
        spamInfoJson = JsonParser().parse(spamInfo).asJsonObject
        messageCountsJson = JsonParser().parse(" { \"fine\": 668, \"spam\": 230 }").asJsonObject
    }
}




