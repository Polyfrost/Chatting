package skytils.skytilsmod.core

object Config {
    var chatTabs = false
    var copyChat = false

    fun markDirty() {
        throw AssertionError()
    }

    fun writeData() {
        throw AssertionError()
    }
}