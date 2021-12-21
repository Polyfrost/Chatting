package com.raeids.stratus.hook

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft

data class ChatTab(
    @SerializedName("name") val name: String,
    @SerializedName("starts") val startsWith: List<String>?,
    @SerializedName("contains") val contains: List<String>?,
    @SerializedName("ends") val endsWith: List<String>?,
    @SerializedName("equals") val equals: List<String>?,
    @SerializedName("regex") val uncompiledRegex: List<String>?,
    @SerializedName("prefix") val prefix: String
) {
    lateinit var button: CleanButton
    var compiledRegex: MutableList<Regex> = arrayListOf()

    //Ugly hack to make GSON not make button / regex null
    fun initialize() {
        compiledRegex = arrayListOf()
        val width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(name)
        button = CleanButton(653452, runBlocking {
            val returnValue = x - 2
            x += 6 + width
            return@runBlocking returnValue
        }, 0, width + 4, 12, this)
        if (uncompiledRegex != null && uncompiledRegex.isNotEmpty()) {
            uncompiledRegex.forEach {
                compiledRegex.add(Regex(it))
            }
        }
    }

    fun shouldRender(message: String): Boolean {
        if (startsWith == null && equals == null && endsWith == null && contains == null && uncompiledRegex == null) {
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
        if ((uncompiledRegex != null) && uncompiledRegex.isNotEmpty()) {
            try {
                compiledRegex.forEach {
                    if (it.matches(message)) {
                        return true
                    }
                }
            } catch (_: Throwable) {

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