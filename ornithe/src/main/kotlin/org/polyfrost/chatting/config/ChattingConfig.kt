package org.polyfrost.chatting.config

import org.lwjgl.input.Keyboard
import org.polyfrost.chatting.Chatting
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper

/**
 * v2's public option names are intentionally retained so existing Chatting
 * profiles continue to deserialize when moved to the current OneConfig API.
 */
object ChattingConfig : Config(
    "chatting.json",
    "/assets/chatting/chatting_dark.svg",
    "Chatting",
    Category.VISUALS,
) {

    @Dropdown(title = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"])
    var textRenderType = 1

    @Switch(title = "Chat Peek", category = "Chat Peek")
    var chatPeek = false

    @Switch(title = "Chat Peek Scrolling", category = "Chat Peek")
    var peekScrolling = true

    @Keybind(title = "Peek KeyBind", category = "Chat Peek")
    var chatPeekBind = KeybindHelper.builder()
        .key(Keyboard.KEY_Z)
        .action { pressed ->
            if (!chatPeek) return@action false
            Chatting.peeking = if (peekMode == 0) pressed else !Chatting.peeking
            false
        }
        .register()

    @RadioButton(title = "Peek Mode", category = "Chat Peek", options = ["Held", "Toggle"])
    var peekMode = 0

    @Switch(title = "Underlined Links", category = "General")
    var underlinedLinks = false

    @Switch(title = "Custom Chat Height", category = "Chat Window")
    var customChatHeight = false

    @Slider(title = "Focused Height (px)", category = "Chat Window", min = 20f, max = 2160f)
    var focusedHeight = 180

    @Slider(title = "Unfocused Height (px)", category = "Chat Window", min = 20f, max = 2160f)
    var unfocusedHeight = 90

    @Switch(title = "Custom Chat Width", category = "Chat Window")
    var customChatWidth = false

    @Slider(title = "Custom Width (px)", category = "Chat Window", min = 20f, max = 2160f)
    var customWidth = 320

    @Color(title = "Chat Button Color", category = "Buttons")
    var chatButtonColor = PolyColor.rgba(255, 255, 255, 255)

    @Color(title = "Chat Button Hovered Color", category = "Buttons")
    var chatButtonHoveredColor = PolyColor.rgba(255, 255, 160, 255)

    @Color(title = "Chat Button Background Color", category = "Buttons")
    var chatButtonBackgroundColor = PolyColor.rgba(0, 0, 0, 128)

    @Color(title = "Chat Button Hovered Background Color", category = "Buttons")
    var chatButtonHoveredBackgroundColor = PolyColor.rgba(255, 255, 255, 128)

    @Switch(title = "Delete Chat History Button", category = "Buttons")
    var chatDeleteHistory = true

    @Switch(title = "Chat Searching", category = "Buttons")
    var chatSearch = true

    @Switch(title = "Hide Chat Scrollbar", category = "General")
    var removeScrollBar = true

    @Switch(title = "Chat Tabs", category = "Tabs")
    var chatTabs = true
        get() = field && (!hypixelOnlyChatTabs || HypixelUtils.isHypixel())

    @Checkbox(title = "Enable Tabs Only on Hypixel", category = "Tabs")
    var hypixelOnlyChatTabs = true

    @Switch(title = "Chat Shortcuts", category = "Shortcuts")
    var chatShortcuts = false
        get() = field && (!hypixelOnlyChatShortcuts || HypixelUtils.isHypixel())

    @Checkbox(title = "Enable Shortcuts Only on Hypixel", category = "Shortcuts")
    var hypixelOnlyChatShortcuts = true

    init {
        addDependency("peekScrolling", "chatPeek")
        addDependency("chatPeekBind", "chatPeek")
        addDependency("peekMode", "chatPeek")
        addDependency("hypixelOnlyChatTabs", "chatTabs")
        addDependency("hypixelOnlyChatShortcuts", "chatShortcuts")
        addDependency("focusedHeight", "customChatHeight")
        addDependency("unfocusedHeight", "customChatHeight")
        addDependency("customWidth", "customChatWidth")
    }
}
