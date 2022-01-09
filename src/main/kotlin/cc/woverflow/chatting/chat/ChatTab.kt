package cc.woverflow.chatting.chat

import cc.woverflow.chatting.gui.components.TabButton
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.runBlocking
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
    val prefix: String
) {
    lateinit var button: TabButton
    lateinit var compiledRegex: ChatRegexes

    //Ugly hack to make GSON not make button / regex null
    fun initialize() {
        compiledRegex = ChatRegexes(uncompiledRegex)
        val width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(name)
        button = TabButton(653452, runBlocking {
            val returnValue = x - 2
            x += 6 + width
            return@runBlocking returnValue
        }, width + 4, 12, this)
    }

    fun shouldRender(chatComponent: IChatComponent): Boolean {
        if (startsWith == null && equals == null && endsWith == null && contains == null && uncompiledRegex == null) {
            return true
        }
        val message =
            if (unformatted) EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.unformattedText) else chatComponent.formattedText
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

    override fun equals(other: Any?): Boolean {
        return other is ChatTab && name == other.name && startsWith == other.startsWith && contains == other.contains && endsWith == other.endsWith && equals == other.equals && compiledRegex == other.compiledRegex
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (startsWith?.hashCode() ?: 0)
        result = 31 * result + (contains?.hashCode() ?: 0)
        result = 31 * result + (endsWith?.hashCode() ?: 0)
        result = 31 * result + (equals?.hashCode() ?: 0)
        result = 31 * result + (uncompiledRegex?.hashCode() ?: 0)
        result = 31 * result + prefix.hashCode()
        result = 31 * result + button.hashCode()
        return result
    }

    companion object {
        private var x = 4
    }
}
