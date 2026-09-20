package org.polyfrost.chatting.chat

//? if > 1.8.9 {
data class ChatRegexes(val regexList: List<String>?) {
    val compiledRegexList: MutableList<Regex> = arrayListOf()

    init {
        regexList?.forEach {
            runCatching { compiledRegexList.add(Regex(it)) }
        }
    }
}
//?} else {
/*data class ChatRegexes(val regexList: List<String>?) {
    val compiledRegexList: MutableList<Regex> = arrayListOf()

    init {
        regexList?.forEach {
            compiledRegexList.add(Regex(it))
        }
    }
}
*///?}
