package org.polyfrost.chatting

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer
import net.ornithemc.osl.keybinds.api.KeybindEvents
import net.ornithemc.osl.keybinds.api.KeybindRegistry
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents
import org.lwjgl.input.Keyboard
import org.polyfrost.chatting.chat.ChatInputBox
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.chat.ChatWindow
import org.polyfrost.chatting.config.ChattingConfig
import java.nio.file.Paths

/** Fabric (Ornithe) client bootstrap for the v2 feature set. */
object Chatting : ClientModInitializer {
    const val ID = "chatting"
    const val NAME = "Chatting"
    const val VER = "2.0.6"

    val keybind = KeyBinding("key.chatting.screenshot", Keyboard.KEY_NONE, "category.chatting")

    var doTheThing = false

    var isPatcher = false

    var isBetterChat = false

    var isSkytils = false

    var isHychat = false

    val chatWindow = ChatWindow()

    val chatInput = ChatInputBox()

    val oldModDir = Paths.get("W-OVERFLOW", NAME)

    var peeking = false
        get() = ChattingConfig.chatPeek && field

    override fun initClient() {
        // Config registration is deferred by OneConfig; explicit preload makes the
        // option tree available before tab/shortcut migration reads its folder.
        ChattingConfig.preload()
        KeybindEvents.REGISTER_KEYBINDS.register { KeybindRegistry.register(keybind) }
        MinecraftClientEvents.TICK_END.register(::onClientTick)
        ChatTabs.initialize()
        ChatShortcuts.initialize()
    }

    private fun onClientTick(client: Minecraft) {
        if (!doTheThing || client.theWorld == null || client.thePlayer == null) return
        // The chat renderer consumes this flag in the same tick. Keeping it here
        // ensures a held screenshot key cannot schedule repeated captures.
        doTheThing = false
    }

    fun getChatHeight(opened: Boolean): Int =
        if (opened) chatWindow.focusedHeight else chatWindow.unfocusedHeight

    fun getChatWidth(): Int = chatWindow.customWidth
}
