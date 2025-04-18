package org.polyfrost.chatting.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils
import club.sk1er.patcher.config.OldPatcherConfig
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.chat.*
import org.polyfrost.chatting.gui.components.TabButton
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.hook.GuiChatHook
import org.polyfrost.chatting.utils.ModCompatHooks
import java.io.File

object ChattingConfig : Config(
    Mod(
        Chatting.NAME,
        ModType.UTIL_QOL,
        "/chatting_dark.svg",
        VigilanceMigrator(File(Chatting.oldModDir, Chatting.ID + ".toml").toPath().toString())
    ), "chatting.json", true, false
) {

    @Dropdown(
        name = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "Specifies how text should be rendered in the chat. Full Shadow displays a shadow on all sides of the text, while Shadow only displays a shadow on the right and bottom sides of the text."
    )
    var textRenderType = 1

    @Color(
        name = "Hover Message Background Color", category = "General",
        description = "The color of the chat background when hovering over a message."
    )
    var hoveredChatBackgroundColor = OneColor(80, 80, 80, 128)

    @Checkbox(
        name = "Message Fade"
    )
    var fade = true

    @Slider(
        name = "Time Before Fade",
        min = 0f, max = 20f
    )
    var fadeTime = 10f

    @Switch(
        name = "Inform Outdated Mods", category = "General",
        description = "Inform the user when a mod can be replaced by Chatting.",
        size = 2
    )
    var informForAlternatives = true

    @Switch(
        name = "Chat Peek",
        description = "Allows you to view / scroll chat while moving around."
    )
    var chatPeek = false

    @Switch(
        name = "Chat Peek Scrolling",
    )
    var peekScrolling = true

    @KeyBind(
        name = "Peek KeyBind"
    )
    var chatPeekBind = OneKeyBind(UKeyboard.KEY_Z)

    @DualOption(
        name = "Peek Mode",
        left = "Held",
        right = "Toggle"
    )
    var peekMode = false

    @Switch(
        name = "Underlined Links", category = "General",
        description = "Makes clickable links in chat blue and underlined.",
        size = 1
    )
    var underlinedLinks = false

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
        name = "Disable for Edits",
        category = "Animations", subcategory = "Messages",
        description = "Disable smooth animations for edited messages."
    )
    var disableSmoothEdits = true

    @Switch(
        name = "Smooth Chat Background",
        category = "Animations", subcategory = "Background",
        description = "Smoothly animate chat background."
    )
    var smoothBG = true

    @Slider(
        name = "Background Animation Duration",
        category = "Animations", subcategory = "Background",
        min = 50f, max = 1000f,
        description = "The speed at which chat background animate."
    )
    var bgDuration = 400f

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

    @Color(
        name = "Chat Button Color", category = "Buttons",
        description = "The color of the chat button."
    )
    var chatButtonColor = OneColor(255, 255, 255, 255)

    @Color(
        name = "Chat Button Hovered Color", category = "Buttons",
        description = "The color of the chat button when hovered."
    )
    var chatButtonHoveredColor = OneColor(255, 255, 160, 255)

    @Color(
        name = "Chat Button Background Color", category = "Buttons",
        description = "The color of the chat button background."
    )
    var chatButtonBackgroundColor = OneColor(0, 0, 0, 128)

    @Color(
        name = "Chat Button Hovered Background Color", category = "Buttons",
        description = "The color of the chat button background when hovered."
    )
    var chatButtonHoveredBackgroundColor = OneColor(255, 255, 255, 128)

    @Switch(
        name = "Button Shadow", category = "Buttons",
        description = "Enable button shadow."
    )
    var buttonShadow = true

    @Switch(
        name = "Extend Chat Background",
        category = "Buttons",
        description = "Extends the chat background if buttons are enabled."
    )
    var extendBG = true

    @Switch(
        name = "Chat Copying Button", category = "Buttons",
        description = "Enable copying chat messages via a button."
    )
    var chatCopy = true

    @Switch(
        name = "Right Click to Copy Chat Message", category = "Buttons",
        description = "Enable right clicking on a chat message to copy it."
    )
    var rightClickCopy = false

    @Switch(
        name = "Only Click Copy Chat Message when Holding CTRL", category = "Buttons",
        description = "Only allow right clicking on a chat message to copy it when holding CTRL."
    )
    var rightClickCopyCtrl = true

    @Switch(
        name = "Delete Chat Message Button", category = "Buttons",
        description = "Enable deleting individual chat messages via a button."
    )
    var chatDelete = true

    @Switch(
        name = "Delete Chat History Button", category = "Buttons",
        description = "Enable deleting chat history via a button."
    )
    var chatDeleteHistory = true

    @Switch(
        name = "Chat Screenshot Button", category = "Buttons",
        description = "Enable taking a screenshot of the chat via a button."
    )
    var chatScreenshot = true

    @Switch(
        name = "Chat Searching", category = "Buttons",
        description = "Enable searching through chat messages."
    )
    var chatSearch = true

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
        text = "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.",
        size = 2,
        category = "Player Chats",
        type = InfoType.INFO
    )
    var ignored = false

    @Info(
        text = "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at. May not be accurate.",
        size = 2,
        category = "Player Chats",
        type = InfoType.INFO
    )
    var ignored1 = false

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

    @HUD(
        name = "Chat Window", category = "Chat Window"
    )
    var chatWindow = ChatWindow()

    @HUD(
        name = "Chat Input Box", category = "Input Box"
    )
    var chatInput = ChatInputBox()

    @Dropdown(
        name = "Screenshot Mode", category = "Screenshotting", options = ["Save To System", "Add To Clipboard", "Both"],
        description = "What to do when taking a screenshot."
    )
    var copyMode = 0

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

    @Info(
        category = "Tabs",
        type = InfoType.INFO,
        text = "You can use the SHIFT key to select multiple tabs, as well as CTRL + TAB to switch to the next tab.",
        size = 2
    )
    @Transient
    var ignored2 = true

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

    var isPatcherMigrated = false
    var isPatcherMigratedPt2CauseImStupid = false

    init {
        initialize()

        try {
            Class.forName("club.sk1er.patcher.config.OldPatcherConfig")
            if (!isPatcherMigrated) {
                if (OldPatcherConfig.transparentChat) {
                    if (OldPatcherConfig.transparentChatOnlyWhenClosed) {
                        chatWindow.setBackgroundColor(chatWindow.getBackgroundColor().also { it.alpha = 0 })
                        chatWindow.differentOpacity = true
                        chatWindow.openOpacity = 0
                    } else {
                        chatWindow.setBackground(false)
                    }
                }
                if (OldPatcherConfig.transparentChatInputField) {
                    chatInput.setBackground(false)
                }
                isPatcherMigrated = true

                save()
            }
            if (!isPatcherMigratedPt2CauseImStupid) {
                if (OldPatcherConfig.transparentChat) {
                    if (OldPatcherConfig.transparentChatOnlyWhenClosed && chatWindow.openOpacity == 255) {
                        chatWindow.openOpacity = 0
                    }
                }

                isPatcherMigratedPt2CauseImStupid = true

                save()
            }
        } catch (_: ClassNotFoundException) {}

        addDependency("rightClickCopyCtrl", "rightClickCopy")
        addDependency("fadeTime", "fade")
        addDependency("offsetNonPlayerMessages", "showChatHeads")
        addDependency("hideChatHeadOnConsecutiveMessages", "showChatHeads")
        addDependency("hypixelOnlyChatTabs", "chatTabs")
        addDependency("hypixelOnlyChatShortcuts", "chatShortcuts")
        addDependency("scrollingSpeed", "smoothScrolling")
        addDependency("messageSpeed", "smoothChat")
        addDependency("disableSmoothEdits", "smoothChat")
        addDependency("bgDuration", "smoothBG")
        addDependency("peekScrolling", "chatPeek")
        addDependency("chatPeekBind", "chatPeek")
        addDependency("peekMode", "chatPeek")
        addDependency("smoothChat", "BetterChat Smooth Chat") {
            !ModCompatHooks.betterChatSmoothMessages
        }
        addListener("peekMode") {
            Chatting.peeking = false
        }
        addListener("hideChatHeadOnConsecutiveMessages") {
            ChatLineHook.`chatting$chatLines`.map { it.get() as ChatLineHook? }.forEach { it?.`chatting$updatePlayerInfo`() }
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
        listOf(
            "chatSearch",
            "chatScreenshot",
            "chatDeleteHistory",
            "chatTabs"
        ).forEach {
            addListener(it) {
            mc.currentScreen?.let { screen ->
                if (screen is GuiChatHook) {
                    screen.`chatting$triggerButtonReset`()
                }
            }
        } }
        // addDependency("showTimestampHover", "showTimestamp")
    }
}
