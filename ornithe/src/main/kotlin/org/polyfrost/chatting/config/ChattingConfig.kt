package org.polyfrost.chatting.config

import org.lwjgl.input.Keyboard
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.ChatDimensions
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

    @Dropdown(
        title = "Text Render Type",
        category = "General",
        options = ["No Shadow", "Shadow"],
        description = "Shadow uses Minecraft's standard chat text shadow; No Shadow renders text flat.",
    )
    var textRenderType = 1
        get() = field.coerceIn(0, 1)

    @Color(
        title = "Chat Background Color",
        category = "General",
        description = "The color of chat message backgrounds; its alpha is combined with vanilla chat opacity.",
    )
    var chatBackgroundColor = PolyColor.rgba(0, 0, 0, 255)

    @Color(
        title = "Hover Message Background Color",
        category = "General",
        description = "The background color of the chat line under the cursor while chat is open.",
    )
    var hoveredChatBackgroundColor = PolyColor.rgba(80, 80, 80, 128)

    @Switch(
        title = "Rounded Chat Corners",
        category = "General",
        description = "Round the outer corners of the visible chat message block.",
    )
    var roundedChatCorners = false

    @Slider(
        title = "Corner Radius",
        category = "General",
        description = "The chat-corner radius in chat pixels.",
        min = 1f,
        max = 16f,
        step = 1f,
    )
    var chatCornerRadius = 6f

    @Switch(
        title = "Message Fade",
        category = "General",
        description = "Fade chat messages after their configured display time.",
    )
    var fade = true

    @Slider(
        title = "Time Before Fade",
        category = "General",
        description = "The number of seconds a message remains visible before it fades; 0 shows messages only while chat is open.",
        min = 0f,
        max = 20f,
    )
    var fadeTime = 10f

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

    @Switch(
        title = "Custom Chat Height",
        category = "Chat Window",
        description = "Set exact focused and unfocused chat heights instead of using Minecraft's chat-height options.",
    )
    var customChatHeight = false

    @Slider(
        title = "Focused Height (px)",
        category = "Chat Window",
        description = "The chat height while chat is open, before Minecraft's chat scale is applied.",
        min = 20f,
        max = 2160f,
        step = 1f,
    )
    var focusedHeight = 180

    @Slider(
        title = "Unfocused Height (px)",
        category = "Chat Window",
        description = "The chat height while chat is closed, before Minecraft's chat scale is applied.",
        min = 20f,
        max = 2160f,
        step = 1f,
    )
    var unfocusedHeight = 90

    @Switch(
        title = "Custom Chat Width",
        category = "Chat Window",
        description = "Set an exact chat width instead of using Minecraft's chat-width option.",
    )
    var customChatWidth = false

    @Slider(
        title = "Chat Width (px)",
        category = "Chat Window",
        description = "The chat width before Minecraft's chat scale is applied.",
        min = 40f,
        max = 2160f,
        step = 1f,
    )
    var customWidth = 320

    @Switch(title = "Smooth Chat Messages", category = "Animations", subcategory = "Messages")
    var smoothChat = true

    @Slider(title = "Message Animation Duration (ms)", category = "Animations", subcategory = "Messages", min = 0f, max = 1000f, step = 50f)
    var smoothChatMs = 500f

    @Switch(title = "Smooth Chat Scrolling", category = "Animations", subcategory = "Scrolling")
    var smoothScrolling = true

    @Slider(title = "Scrolling Animation Duration (ms)", category = "Animations", subcategory = "Scrolling", min = 0f, max = 1000f, step = 50f)
    var smoothScrollingMs = 150f

    @Color(title = "Chat Button Color", category = "Buttons")
    var chatButtonColor = PolyColor.rgba(255, 255, 255, 255)

    @Color(title = "Chat Button Hovered Color", category = "Buttons")
    var chatButtonHoveredColor = PolyColor.rgba(255, 255, 160, 255)

    @Color(title = "Chat Button Background Color", category = "Buttons")
    var chatButtonBackgroundColor = PolyColor.rgba(0, 0, 0, 128)

    @Color(title = "Chat Button Hovered Background Color", category = "Buttons")
    var chatButtonHoveredBackgroundColor = PolyColor.rgba(255, 255, 255, 128)

    @Switch(
        title = "Button Shadow",
        category = "Buttons",
        description = "Draw a one-pixel shadow behind chat button icons.",
    )
    var buttonShadow = true

    @Switch(
        title = "Chat Copying Button",
        category = "Buttons",
        description = "Show a copy button beside the chat line under the cursor while chat is open.",
    )
    var chatCopy = true

    @Switch(
        title = "Delete Chat Message Button",
        category = "Buttons",
        description = "Show a delete button beside the chat line under the cursor while chat is open.",
    )
    var chatDelete = true

    @Switch(
        title = "Right Click to Copy Chat Message",
        category = "Buttons",
        description = "Copy a chat message by right-clicking its text.",
    )
    var rightClickCopy = false

    @Switch(
        title = "Only Right Click Copy When Holding Ctrl",
        category = "Buttons",
        description = "Require Ctrl while right-clicking a message to copy it.",
    )
    var rightClickCopyCtrl = true

    @Switch(
        title = "Delete Chat History Button",
        category = "Buttons",
        description = "Show a button that immediately clears the local chat history.",
    )
    var chatDeleteHistory = true

    @Switch(title = "Chat Searching", category = "Buttons")
    var chatSearch = true

    @Switch(title = "Remove Scroll Bar", category = "Animations", subcategory = "Scrolling")
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
        addDependency("rightClickCopyCtrl", "rightClickCopy")
        addCallback("customChatHeight") { ChatDimensions.refresh() }
        addCallback("focusedHeight") { ChatDimensions.refresh() }
        addCallback("unfocusedHeight") { ChatDimensions.refresh() }
        addCallback("customChatWidth") { ChatDimensions.refresh() }
        addCallback("customWidth") { ChatDimensions.refresh() }
        addDependency("smoothChatMs", "smoothChat")
        addDependency("smoothScrollingMs", "smoothScrolling")
        addDependency("chatCornerRadius", "roundedChatCorners")
        addDependency("fadeTime", "fade")
    }
}
