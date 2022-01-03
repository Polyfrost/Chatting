package cc.woverflow.chatting.utils

class ListenableArrayList<T>(private val runnable: (ListenableArrayList<T>) -> Unit, vararg elements: T): ArrayList<T>() {
    override fun add(element: T): Boolean {
        val value = super.add(element)
        runnable.invoke(this)
        return value
    }
}