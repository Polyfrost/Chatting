package org.polyfrost.chatting.chat

import com.google.gson.annotations.SerializedName
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import java.util.Locale

data class ChatTab(
    val enabled: Boolean,
    val name: String,
    val unformatted: Boolean,
    val lowercase: Boolean?,
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
    val color: Int?,
    @SerializedName("hovered_color") val hoveredColor: Int?,
    @SerializedName("selected_color") val selectedColor: Int?,
    val prefix: String?
) {
    @Transient
    lateinit var compiledRegex: ChatRegexes

    @Transient
    lateinit var compiledIgnoreRegex: ChatRegexes

    @Transient
    var messages: List<String>? = ArrayList()

    /** GSON skips the `@Transient` fields, so compile the regexes after deserialisation. */
    fun initialize() {
        compiledRegex = ChatRegexes(uncompiledRegex)
        compiledIgnoreRegex = ChatRegexes(uncompiledIgnoreRegex)
    }

    fun shouldRender(chatComponent: Component): Boolean {
        val message =
            (if (unformatted) ChatFormatting.stripFormatting(chatComponent.string) ?: chatComponent.string
            else LegacyText.toFormatted(chatComponent)).let {
                if (lowercase == true) it.lowercase(Locale.ENGLISH) else it
            }
        ignoreStartsWith?.forEach { if (message.startsWith(it)) return false }
        ignoreEquals?.forEach { if (message == it) return false }
        ignoreEndsWith?.forEach { if (message.endsWith(it)) return false }
        ignoreContains?.forEach { if (message.contains(it)) return false }
        compiledIgnoreRegex.compiledRegexList.forEach { if (it.matches(message)) return false }
        if (startsWith.isNullOrEmpty() && equals.isNullOrEmpty() && endsWith.isNullOrEmpty() && contains.isNullOrEmpty() && uncompiledRegex.isNullOrEmpty()) {
            return true
        }
        equals?.forEach { if (message == it) return true }
        startsWith?.forEach { if (message.startsWith(it)) return true }
        endsWith?.forEach { if (message.endsWith(it)) return true }
        contains?.forEach { if (message.contains(it)) return true }
        compiledRegex.compiledRegexList.forEach { if (it.matches(message)) return true }
        return false
    }

    companion object {
        const val COLOR: Int = 14737632
        const val HOVERED_COLOR: Int = 16777120
        const val SELECTED_COLOR: Int = 10526880
    }
}
