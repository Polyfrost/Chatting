package cc.woverflow.chatting.chat

import cc.woverflow.chatting.gui.components.TabButton
import com.google.gson.annotations.SerializedName
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent

data class ChatTab(
    val enabled: Boolean,
    val name: String,
    val unformatted: Boolean,
    @SerializedName("starts") val startsWith: List<String>?,
    val contains: List<String>?,
    @SerializedName("ends") val endsWith: List<String>?,
    val equals: List<String>?,
    @SerializedName("regex") val uncompiledRegex: List<String>?,
    @SerializedName("ignore_starts") val ignoreStartsWith: List<String>?,
    @SerializedName("ignore_contains") val ignoreContains: List<String>?,
    @SerializedName("ignore_ends") val ignoreEndsWith: List<String>?,
    @SerializedName("ignore_equals") val ignoreEquals: List<String>?,
    @SerializedName("ignore_regex") val uncompiledIgnoreRegex: List<String>?,
    val prefix: String,
) {
    lateinit var button: TabButton
    lateinit var compiledRegex: ChatRegexes
    lateinit var compiledIgnoreRegex: ChatRegexes

    //Ugly hack to make GSON not make button / regex null
    fun initialize() {
        compiledRegex = ChatRegexes(uncompiledRegex)
        compiledIgnoreRegex = ChatRegexes(uncompiledIgnoreRegex)
        val width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(name)
        button = TabButton(653452, run {
            val returnValue = x - 2
            x += 6 + width
            return@run returnValue
        }, width + 4, 12, this)
    }

    fun shouldRender(chatComponent: IChatComponent): Boolean {
        val message =
            if (unformatted) EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.unformattedText) else chatComponent.formattedText
        ignoreStartsWith?.forEach {
            if (message.startsWith(it)) {
                return false
            }
        }
        ignoreEquals?.forEach {
            if (message == it) {
                return false
            }
        }
        ignoreEndsWith?.forEach {
            if (message.endsWith(it)) {
                return false
            }
        }
        ignoreContains?.forEach {
            if (message.contains(it)) {
                return false
            }
        }
        compiledIgnoreRegex.compiledRegexList.forEach {
            if (it.matches(message)) {
                return false
            }
        }
        if (startsWith.isNullOrEmpty() && equals.isNullOrEmpty() && endsWith.isNullOrEmpty() && contains.isNullOrEmpty() && uncompiledRegex.isNullOrEmpty()) {
            return true
        }
        equals?.forEach {
            if (message == it) {
                return true
            }
        }
        startsWith?.forEach {
            if (message.startsWith(it)) {
                return true
            }
        }
        endsWith?.forEach {
            if (message.endsWith(it)) {
                return true
            }
        }
        contains?.forEach {
            if (message.contains(it)) {
                return true
            }
        }
        compiledRegex.compiledRegexList.forEach {
            if (it.matches(message)) {
                return true
            }
        }
        return false
    }

    companion object {
        private var x = 4
    }
}
