package com.raeids.stratus.chat

data class ChatRegexes(val regexList: List<String>?) {
    val compiledRegexList: MutableList<Regex> = arrayListOf()

    init {
        regexList?.forEach {
            compiledRegexList.add(Regex(it))
        }
    }
}
