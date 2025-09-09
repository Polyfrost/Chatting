package org.polyfrost.chatting

import dev.deftu.omnicore.client.OmniKeyboard
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.input.KeybindHelper

object ModConfig : Config("${ModConstants.ID}.json", ModConstants.NAME, Category.OTHER) {

    @Dropdown(
        title = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "Specifies how text should be rendered in the chat. Full Shadow displays a shadow on all sides of the text, while Shadow only displays a shadow on the right and bottom sides of the text."
    )
    var textRenderType = 1

    @Color(
        title = "Chat Background Color", category = "General",
        description = "The color of the chat background."
    )
    var chatBackgroundColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Hover Message Background Color", category = "General",
        description = "The color of the chat background when hovering over a message."
    )
    var hoveredChatBackgroundColor = rgba(80, 80, 80, 0.5f)

    @Checkbox(
        title = "Message Fade"
    )
    var fade = true

    @Slider(
        title = "Time Before Fade",
        min = 0f, max = 20f
    )
    var fadeTime = 10f

    @Switch(
        title = "Inform Outdated Mods", category = "General",
        description = "Inform the user when a mod can be replaced by Chatting."
    )
    var informForAlternatives = true

    @Switch(
        title = "Chat Peek",
        description = "Allows you to view / scroll chat while moving around."
    )
    var chatPeek = false

    @Switch(
        title = "Chat Peek Scrolling",
    )
    var peekScrolling = true

    @Keybind(
        title = "Peek KeyBind"
    )
    var chatPeekBind = KeybindHelper.builder().keys(OmniKeyboard.KEY_Z).does {
    }.build()

    @RadioButton(
        title = "Peek Mode",
        options = ["Held", "Toggle"]
    )
    var peekMode = 0

    @Switch(
        title = "Underlined Links", category = "General",
        description = "Makes clickable links in chat blue and underlined."
    )
    var underlinedLinks = false

    @Switch(
        title = "Smooth Chat Messages",
        category = "Animations", subcategory = "Messages",
        description = "Smoothly animate chat messages when they appear."
    )
    var smoothChat = true

    @Slider(
        title = "Message Animation Speed",
        category = "Animations", subcategory = "Messages",
        min = 0.0f, max = 1.0f,
        description = "The speed at which chat messages animate."
    )
    var messageSpeed = 0.5f

    @Switch(
        title = "Disable for Edits",
        category = "Animations", subcategory = "Messages",
        description = "Disable smooth animations for edited messages."
    )
    var disableSmoothEdits = true

    @Switch(
        title = "Smooth Chat Background",
        category = "Animations", subcategory = "Background",
        description = "Smoothly animate chat background."
    )
    var smoothBG = true

    @Slider(
        title = "Background Animation Duration",
        category = "Animations", subcategory = "Background",
        min = 50f, max = 1000f,
        description = "The speed at which chat background animate."
    )
    var bgDuration = 400f

    @Switch(
        title = "Smooth Chat Scrolling",
        category = "Animations", subcategory = "Scrolling",
        description = "Smoothly animate scrolling when scrolling through the chat."
    )
    var smoothScrolling = true

    @Slider(
        title = "Scrolling Animation Speed",
        category = "Animations", subcategory = "Scrolling",
        min = 0.0f, max = 1.0f,
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
    var chatButtonColor = rgba(255, 255, 255, 1f)

    @Color(
        title = "Chat Button Hovered Color", category = "Buttons",
        description = "The color of the chat button when hovered."
    )
    var chatButtonHoveredColor = rgba(255, 255, 160, 1f)

    @Color(
        title = "Chat Button Background Color", category = "Buttons",
        description = "The color of the chat button background."
    )
    var chatButtonBackgroundColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Chat Button Hovered Background Color", category = "Buttons",
        description = "The color of the chat button background when hovered."
    )
    var chatButtonHoveredBackgroundColor = rgba(255, 255, 255, 0.5f)

    @Switch(
        title = "Button Shadow", category = "Buttons",
        description = "Enable button shadow."
    )
    var buttonShadow = true

    @Switch(
        title = "Extend Chat Background",
        category = "Buttons",
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
        title = "Show Chat Heads", description = "Show the chat heads of players in chat", category = "Chat Heads",
    )
    var showChatHeads = true

    @Switch(
        title = "Offset Non-Player Messages",
        description = "Offset all messages, even if a player has not been detected.",
        category = "Chat Heads"
    )
    var offsetNonPlayerMessages = false

    @Switch(
        title = "Hide Chat Head on Consecutive Messages",
        description = "Hide the chat head if the previous message was from the same player.",
        category = "Chat Heads"
    )
    var hideChatHeadOnConsecutiveMessages = true

    @Info(
        title = "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.",
        category = "Player Chats",
        description = ""
    )
    var ignored = false

    @Info(
        title = "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at. May not be accurate.",
        category = "Player Chats",
        description = ""
    )
    var ignored1 = false

    @Slider(
        min = 80F, max = 100F, title = "Spam Blocker Threshold", category = "Player Chats"
    )
    var spamThreshold = 100

    @Switch(
        title = "Custom SkyBlock Chat Formatting (remove ranks)", category = "Player Chats"
    )
    var customChatFormatting = false

    @Switch(
        title = "Completely Hide Spam", category = "Player Chats"
    )
    var hideSpam = false

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
            return if (hypixelOnlyChatTabs) {
                HypixelUtils.isHypixel()
            } else {
                true
            }
        }

    @Checkbox(
        title = "Enable Tabs Only on Hypixel", category = "Tabs",
        description = "Only enable chat tabs on Hypixel"
    )
    var hypixelOnlyChatTabs = true

    @Info(
        category = "Tabs",
        title = "You can use the SHIFT key to select multiple tabs, as well as CTRL + TAB to switch to the next tab.",
        description = ""
    )
    @Transient
    var ignored2 = true

    @Switch(
        title = "Chat Shortcuts", category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) {
                HypixelUtils.isHypixel()
            } else {
                true
            }
        }

    @Checkbox(
        title = "Enable Shortcuts Only on Hypixel", category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Switch(
        title = "Remove Tooltip Background", category = "Tooltips",
        description = "Removes the background from tooltips."
    )
    var removeTooltipBackground = false

    @Dropdown(
        title = "Tooltip Text Render Type", category = "Tooltips", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "The type of shadow to render on tooltips."
    )
    var tooltipTextRenderType = 1

}
