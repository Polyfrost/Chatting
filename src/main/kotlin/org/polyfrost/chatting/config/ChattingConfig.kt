package org.polyfrost.chatting.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatTab
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.gui.components.TabButton
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.utils.ModCompatHooks
import java.io.File

object ChattingConfig : Config(
    Mod(
        Chatting.NAME,
        ModType.UTIL_QOL,
        "/chatting_dark.svg",
        VigilanceMigrator(File(Chatting.oldModDir, Chatting.ID + ".toml").toPath().toString())
    ), "chatting.json"
) {

    @Dropdown(
        name = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "Specifies how text should be rendered in the chat. Full Shadow displays a shadow on all sides of the text, while Shadow only displays a shadow on the right and bottom sides of the text."
    )
    var textRenderType = 1

    @Color(
        name = "Chat Background Color", category = "General",
        description = "The color of the chat background."
    )
    var chatBackgroundColor = OneColor(0, 0, 0, 128)

    @Color(
        name = "Copy Chat Message Background Color", category = "General",
        description = "The color of the chat background when hovering over a message."
    )
    var hoveredChatBackgroundColor = OneColor(80, 80, 80, 128)

    @Switch(
        name = "Right Click to Copy Chat Message", category = "General",
        description = "Enable right clicking on a chat message to copy it."
    )
    var rightClickCopy = false

    @Switch(
        name = "Compact Input Box", category = "General",
        description = "Make the chat input box the same width as the chat box."
    )
    var compactInputBox = false

    @Color(
        name = "Input Box Background Color", category = "General",
        description = "The color of the chat input box background."
    )
    var inputBoxBackgroundColor = OneColor(0, 0, 0, 128)

    @Color(
        name = "Chat Button Background Color", category = "General",
        description = "The color of the chat button background."
    )
    var chatButtonBackgroundColor = OneColor(0, 0, 0, 128)

    @Color(
        name = "Chat Button Hovered Background Color", category = "General",
        description = "The color of the chat button background when hovered."
    )
    var chatButtonHoveredBackgroundColor = OneColor(255, 255, 255, 128)

    @Switch(
        name = "Inform Outdated Mods", category = "General",
        description = "Inform the user when a mod can be replaced by Chatting."
    )
    var informForAlternatives = true

    @Switch(
        name = "Smooth Chat Messages",
        category = "Animations", subcategory = "Messages",
        description = "Smoothly animate chat messages when they appear."
    )
    var smoothChat = true

    @Slider(
        name = "Message Animation Speed",
        category = "Animations", subcategory = "Messages",
        min = 0.0f, max = 1.0f,
        description = "The speed at which chat messages animate."
    )
    var messageSpeed = 0.5f

    @Switch(
        name = "Smooth Chat Scrolling",
        category = "Animations", subcategory = "Scrolling",
        description = "Smoothly animate scrolling when scrolling through the chat."
    )
    var smoothScrolling = true

    @Slider(
        name = "Scrolling Animation Speed",
        category = "Animations", subcategory = "Scrolling",
        min = 0.0f, max = 1.0f,
        description = "The speed at which scrolling animates."
    )
    var scrollingSpeed = 0.15f

    @Switch(
        name = "Remove Scroll Bar",
        category = "Animations", subcategory = "Scrolling",
        description = "Removes the vanilla scroll bar from the chat."
    )
    var removeScrollBar = true

    @Switch(
        name = "Show Chat Heads", description = "Show the chat heads of players in chat", category = "Chat Heads",
    )
    var showChatHeads = true

    @Switch(
        name = "Offset Non-Player Messages",
        description = "Offset all messages, even if a player has not been detected.",
        category = "Chat Heads"
    )
    var offsetNonPlayerMessages = false

    @Switch(
        name = "Hide Chat Head on Consecutive Messages",
        description = "Hide the chat head if the previous message was from the same player.",
        category = "Chat Heads"
    )
    var hideChatHeadOnConsecutiveMessages = true

    /*/
    @Property(
        type = PropertyType.SWITCH,
        name = "Show Timestamp",
        description = "Show message timestamp.",
        category = "General"
    )
    var showTimestamp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Timestamp Only On Hover",
        description = "Show timestamp only on mouse hover.",
        category = "General"
    )
    var showTimestampHover = true

     */

    @Info(
        text = "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.\n" + "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at.\n" + "Note that this is not and never will be 100% accurate; however, it's pretty much guaranteed to block most spam.",
        size = 2,
        category = "Player Chats",
        type = InfoType.INFO
    )
    var ignored = false

    @Slider(
        min = 80F, max = 100F, name = "Spam Blocker Threshold", category = "Player Chats"
    )
    var spamThreshold = 100

    @Switch(
        name = "Custom SkyBlock Chat Formatting (remove ranks)", category = "Player Chats"
    )
    var customChatFormatting = false

    @Switch(
        name = "Completely Hide Spam", category = "Player Chats"
    )
    var hideSpam = false

    @Switch(
        name = "Custom Chat Height", category = "Chat Window",
        description = "Set a custom height for the chat window. Allows for more customization than the vanilla chat height options."
    )
    var customChatHeight = false

    @Slider(
        min = 180F, max = 2160F, name = "Focused Height (px)", category = "Chat Window",
        description = "The height of the chat window when focused."
    )
    var focusedHeight = 180

    @Slider(
        min = 180F, max = 2160F, name = "Unfocused Height (px)", category = "Chat Window",
        description = "The height of the chat window when unfocused."
    )
    var unfocusedHeight = 180

    @Dropdown(
        name = "Screenshot Mode", category = "Screenshotting", options = ["Save To System", "Add To Clipboard", "Both"],
        description = "What to do when taking a screenshot."
    )
    var copyMode = 0

    @Checkbox(
        name = "Chat Searching", category = "Searching",
        description = "Enable searching through chat messages."
    )
    var chatSearch = true

    @Switch(
        name = "Chat Tabs", category = "Tabs",
        description = "Allow filtering chat messages by a tab."
    )
    var chatTabs = true
        get() {
            if (!field) return false
            return if (hypixelOnlyChatTabs) {
                HypixelUtils.INSTANCE.isHypixel
            } else {
                true
            }
        }

    @Checkbox(
        name = "Enable Tabs Only on Hypixel", category = "Tabs",
        description = "Only enable chat tabs on Hypixel"
    )
    var hypixelOnlyChatTabs = true

    @Switch(
        name = "Chat Shortcuts", category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) {
                HypixelUtils.INSTANCE.isHypixel
            } else {
                true
            }
        }

    @Checkbox(
        name = "Enable Shortcuts Only on Hypixel", category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Switch(
        name = "Remove Tooltip Background", category = "Tooltips",
        description = "Removes the background from tooltips."
    )
    var removeTooltipBackground = false

    @Dropdown(
        name = "Tooltip Text Render Type", category = "Tooltips", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "The type of shadow to render on tooltips."
    )
    var tooltipTextRenderType = 1

    init {
        initialize()
        addDependency("offsetNonPlayerMessages", "showChatHeads")
        addDependency("hideChatHeadOnConsecutiveMessages", "showChatHeads")
        addDependency("hypixelOnlyChatTabs", "chatTabs")
        addDependency("hypixelOnlyChatShortcuts", "chatShortcuts")
        addDependency("focusedHeight", "customChatHeight")
        addDependency("unfocusedHeight", "customChatHeight")
        addDependency("scrollingSpeed", "smoothScrolling")
        addDependency("messageSpeed", "smoothChat")
        addDependency("smoothChat", "BetterChat Smooth Chat") {
            return@addDependency !ModCompatHooks.betterChatSmoothMessages
        }
        addListener("hideChatHeadOnConsecutiveMessages") {
            ChatLineHook.chatLines.map { it.get() as ChatLineHook? }.forEach { it?.updatePlayerInfo() }
        }
        addListener("chatTabs") {
            ChatTabs.initialize()
            if (!chatTabs) {
                val dummy = ChatTab(
                    true,
                    "ALL",
                    unformatted = false,
                    lowercase = false,
                    startsWith = null,
                    contains = null,
                    endsWith = null,
                    equals = null,
                    uncompiledRegex = null,
                    ignoreStartsWith = null,
                    ignoreContains = null,
                    ignoreEndsWith = null,
                    ignoreEquals = null,
                    uncompiledIgnoreRegex = null,
                    color = TabButton.color,
                    hoveredColor = TabButton.hoveredColor,
                    selectedColor = TabButton.selectedColor,
                    prefix = ""
                )
                dummy.initialize()
                ChatTabs.currentTabs.clear()
                ChatTabs.currentTabs.add(dummy)
            } else {
                ChatTabs.currentTabs.clear()
                ChatTabs.currentTabs.add(ChatTabs.tabs[0])
            }
        }
        addListener("chatShortcuts") {
            ChatShortcuts.initialize()
        }
        // addDependency("showTimestampHover", "showTimestamp")
    }
}
