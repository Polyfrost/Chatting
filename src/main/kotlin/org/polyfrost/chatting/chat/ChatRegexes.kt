package org.polyfrost.chatting.chat

data class ChatRegexes(val regexList: List<String>?) {
    val compiledRegexList: MutableList<Regex> = arrayListOf()

    init {
        regexList?.forEach {
            runCatching { compiledRegexList.add(Regex(it)) }
        }
    }
}
