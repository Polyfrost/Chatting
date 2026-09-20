package org.polyfrost.chatting

//? if > 1.8.9 {
object ChattingConstants {

    // replaced at build time from gradle.properties by the bloom DGT plugin
    const val ID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

}
//?} else {
/*/** Build and identity values shared by the modern feature modules. */
object ChattingConstants {
    const val ID = "chatting"
    const val NAME = "Chatting"
    const val VERSION = "2.0.6+mc1.8.9-ornithe"
}
*///?}
