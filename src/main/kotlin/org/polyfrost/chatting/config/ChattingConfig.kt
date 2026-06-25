package org.polyfrost.chatting.config

import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.ChatTabs

object ChattingConfig : Config(
    "chatting.json",
    "/assets/chatting/chatting_dark.svg",
    "Chatting",
    Category.QOL,
) {

    @Dropdown(
        title = "Text Render Type", category = "General", options = ["No Shadow", "Shadow"],
        description = "Specifies how text should be rendered in the chat. Shadow displays a drop shadow behind the text (the vanilla style); No Shadow renders the text flat."
    )
    var textRenderType = 1

    @Color(
        title = "Hover Message Background Color", category = "General",
        description = "The color of the chat background when hovering over a message."
    )
    var hoveredChatBackgroundColor = PolyColor.rgba(80, 80, 80, 128)

    @Switch(
        title = "Message Fade", category = "General",
        description = "Fade out chat messages after a period of time."
    )
    var fade = true

    @Slider(
        title = "Time Before Fade", category = "General",
        min = 0f, max = 20f
    )
    var fadeTime = 10f

    @Switch(
        title = "Underlined Links", category = "General",
        description = "Makes clickable links in chat blue and underlined.",
    )
    var underlinedLinks = false

    @Include
    var chatWindowMoved = false

    @Switch(
        title = "Chat Peek", category = "Chat Peek",
        description = "Allows you to view / scroll chat while moving around."
    )
    var chatPeek = false

    @Switch(
        title = "Chat Peek Scrolling", category = "Chat Peek",
    )
    var peekScrolling = true

    @Keybind(
        title = "Peek KeyBind", category = "Chat Peek"
    )
    var chatPeekBind = KeybindHelper.builder().action(this::runPeek).register()

    @RadioButton(
        title = "Peek Mode", category = "Chat Peek",
        options = ["Held", "Toggle"],
    )
    var peekMode = 0

    @Switch(
        title = "Smooth Chat Messages",
        category = "Animations", subcategory = "Messages",
        description = "Smoothly animate chat messages when they appear."
    )
    var smoothChat = true

    @Slider(
        title = "Message Animation Speed",
        category = "Animations", subcategory = "Messages",
        min = 0.0f, max = 1.0f, step = 0.05f,
        description = "The speed at which chat messages animate."
    )
    var messageSpeed = 0.5f

    @Switch(
        title = "Smooth Chat Scrolling",
        category = "Animations", subcategory = "Scrolling",
        description = "Smoothly animate scrolling when scrolling through the chat."
    )
    var smoothScrolling = true

    @Slider(
        title = "Scrolling Animation Speed",
        category = "Animations", subcategory = "Scrolling",
        min = 0.0f, max = 1.0f, step = 0.05f,
        description = "The speed at which scrolling animates."
    )
    var scrollingSpeed = 0.15f

    @Switch(
        title = "Remove Scroll Bar",
        category = "Animations", subcategory = "Scrolling",
        description = "Removes the vanilla scroll bar from the chat."
    )
    var removeScrollBar = true

    @Color(
        title = "Chat Button Color", category = "Buttons",
        description = "The color of the chat button."
    )
    var chatButtonColor = PolyColor.rgba(255, 255, 255, 255)

    @Color(
        title = "Chat Button Hovered Color", category = "Buttons",
        description = "The color of the chat button when hovered."
    )
    var chatButtonHoveredColor = PolyColor.rgba(255, 255, 160, 255)

    @Color(
        title = "Chat Button Background Color", category = "Buttons",
        description = "The color of the chat button background."
    )
    var chatButtonBackgroundColor = PolyColor.rgba(0, 0, 0, 128)

    @Color(
        title = "Chat Button Hovered Background Color", category = "Buttons",
        description = "The color of the chat button background when hovered."
    )
    var chatButtonHoveredBackgroundColor = PolyColor.rgba(255, 255, 255, 128)

    @Switch(
        title = "Button Shadow", category = "Buttons",
        description = "Enable button shadow."
    )
    var buttonShadow = true

    @Switch(
        title = "Extend Chat Background", category = "Buttons",
        description = "Extends the chat background if buttons are enabled."
    )
    var extendBG = true

    @Switch(
        title = "Chat Copying Button", category = "Buttons",
        description = "Enable copying chat messages via a button."
    )
    var chatCopy = true

    @Switch(
        title = "Right Click to Copy Chat Message", category = "Buttons",
        description = "Enable right clicking on a chat message to copy it."
    )
    var rightClickCopy = false

    @Switch(
        title = "Only Click Copy Chat Message when Holding CTRL", category = "Buttons",
        description = "Only allow right clicking on a chat message to copy it when holding CTRL."
    )
    var rightClickCopyCtrl = true

    @Switch(
        title = "Delete Chat Message Button", category = "Buttons",
        description = "Enable deleting individual chat messages via a button."
    )
    var chatDelete = true

    @Switch(
        title = "Delete Chat History Button", category = "Buttons",
        description = "Enable deleting chat history via a button."
    )
    var chatDeleteHistory = true

    @Switch(
        title = "Chat Screenshot Button", category = "Buttons",
        description = "Enable taking a screenshot of the chat via a button."
    )
    var chatScreenshot = true

    @Switch(
        title = "Chat Searching", category = "Buttons",
        description = "Enable searching through chat messages."
    )
    var chatSearch = true

    @Switch(
        title = "Show Chat Heads", category = "Chat Heads",
        description = "Show the chat heads of players in chat",
    )
    var showChatHeads = true

    @Switch(
        title = "Offset Non-Player Messages", category = "Chat Heads",
        description = "Offset all messages, even if a player has not been detected.",
    )
    var offsetNonPlayerMessages = false

    @Switch(
        title = "Hide Chat Head on Consecutive Messages", category = "Chat Heads",
        description = "Hide the chat head if the previous message was from the same player.",
    )
    var hideChatHeadOnConsecutiveMessages = true

    @Dropdown(
        title = "Screenshot Mode", category = "Screenshotting", options = ["Save To System", "Add To Clipboard", "Both"],
        description = "What to do when taking a screenshot."
    )
    var copyMode = 0

    @Switch(
        title = "Chat Tabs", category = "Tabs",
        description = "Allow filtering chat messages by a tab."
    )
    var chatTabs = true
        get() {
            if (!field) return false
            return if (hypixelOnlyChatTabs) isHypixel() else true
        }

    @Checkbox(
        title = "Enable Tabs Only on Hypixel", category = "Tabs",
        description = "Only enable chat tabs on Hypixel"
    )
    var hypixelOnlyChatTabs = true

    @Switch(
        title = "Chat Shortcuts", category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) isHypixel() else true
        }

    @Checkbox(
        title = "Enable Shortcuts Only on Hypixel", category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    private fun isHypixel(): Boolean {
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip.contains("hypixel", ignoreCase = true)
    }

    private fun runPeek(pressed: Boolean): Boolean {
        if (!chatPeek) return false
        if (peekMode == 0 /* HOLD */) {
            Chatting.peeking = pressed
            if (!pressed) resetPeekScroll()
        } else if (pressed) {
            Chatting.peeking = !Chatting.peeking
            if (!Chatting.peeking) resetPeekScroll()
        }
        return false
    }

    private fun resetPeekScroll() {
        //? if >=26.2 {
        /*Minecraft.getInstance().gui?.hud?.chat?.resetChatScroll()
        *///?} else {
        Minecraft.getInstance().gui?.chat?.resetChatScroll()
        //?}
    }

    init {
        addDependency("rightClickCopyCtrl", "rightClickCopy")
        addDependency("fadeTime", "fade")
        addDependency("offsetNonPlayerMessages", "showChatHeads")
        addDependency("hideChatHeadOnConsecutiveMessages", "showChatHeads")
        addDependency("hypixelOnlyChatTabs", "chatTabs")
        addDependency("hypixelOnlyChatShortcuts", "chatShortcuts")
        addDependency("scrollingSpeed", "smoothScrolling")
        addDependency("messageSpeed", "smoothChat")
        addDependency("peekScrolling", "chatPeek")
        addDependency("chatPeekBind", "chatPeek")
        addDependency("peekMode", "chatPeek")

        addCallback("peekMode") { Chatting.peeking = false }
        addCallback("chatTabs") { ChatTabs.refresh() }
        addCallback("hypixelOnlyChatTabs") { ChatTabs.refresh() }
    }
}
