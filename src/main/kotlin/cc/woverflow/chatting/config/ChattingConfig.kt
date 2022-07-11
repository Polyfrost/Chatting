package cc.woverflow.chatting.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.utils.dsl.openScreen
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils
import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.chat.ChatShortcuts
import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.gui.ChatShortcutViewGui
import cc.woverflow.chatting.gui.components.TabButton

import java.io.File

object ChattingConfig :
    Config(
        Mod(Chatting.NAME, ModType.UTIL_QOL, VigilanceMigrator(File(Chatting.modDir, Chatting.ID + ".toml").toPath().toString())),
        "chatting.json"
    ) {

    @Dropdown(
        name = "Text Render Type",
        category = "General",
        options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var textRenderType = 1

    @Switch(
        name = "Remove Tooltip Background",
        category = "General"
    )
    var removeTooltipBackground = false

    @Switch(
        name = "Remove Scroll Bar",
        category = "General"
    )
    var removeScrollBar = false

    @cc.polyfrost.oneconfig.config.annotations.Color(
        name = "Chat Background Color",
        category = "General",
        allowAlpha = false
    )
    var chatBackgroundColor = OneColor(0, 0, 0, 128)

    @cc.polyfrost.oneconfig.config.annotations.Color(
        name = "Copy Chat Message Background Color",
        category = "General",
        allowAlpha = false
    )
    var hoveredChatBackgroundColor = OneColor(80, 80, 80, 128)

    @Switch(
        name = "Compact Input Box",
        category = "General"
    )
    var compactInputBox = false

    @Switch(
        name = "Inform Outdated Mods",
        category = "General"
    )
    var informForAlternatives = true

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
        text = "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.\n" +
                "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at.\n" +
                "Note that this is not and never will be 100% accurate; however, it's pretty much guaranteed to block most spam.",
        size = 2, category = "Player Chats",
        type = InfoType.INFO
    )
    var ignored = false

    @Slider(
        min = 80F,
        max = 100F,
        name = "Spam Blocker Threshold",
        category = "Player Chats"
    )
    var spamThreshold = 100

    @Switch(
        name = "Custom SkyBlock Chat Formatting (remove ranks)",
        category = "Player Chats"
    )
    var customChatFormatting = false

    @Switch(
        name = "Completely Hide Spam",
        category = "Player Chats"
    )
    var hideSpam = false

    @Switch(
        name = "Custom Chat Height",
        category = "Chat Window"
    )
    var customChatHeight = true

    @Slider(
        min = 180F,
        max = 2160F,
        name = "Focused Height (px)",
        category = "Chat Window"
    )
    var focusedHeight = 180

    @Slider(
        min = 180F,
        max = 2160F,
        name = "Unfocused Height (px)",
        category = "Chat Window"
    )
    var unfocusedHeight = 180

    @Dropdown(
        name = "Screenshot Mode",
        category = "Screenshotting",
        options = ["Save To System", "Add To Clipboard", "Both"]
    )
    var copyMode = 0

    @Checkbox(
        name = "Chat Searching",
        category = "Searching"
    )
    var chatSearch = true

    @Switch(
        name = "Chat Tabs",
        category = "Tabs"
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
        name = "Enable Tabs Only on Hypixel",
        category = "Tabs"
    )
    var hypixelOnlyChatTabs = true

    @Switch(
        name = "Chat Shortcuts",
        category = "Shortcuts"
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
        name = "Enable Shortcuts Only on Hypixel",
        category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Button(
        name = "Open Chat Shortcuts Editor GUI",
        category = "Shortcuts", text = "Open"
    )
    var openChatShortcutsGUI = Runnable { ChatShortcutViewGui().openScreen() }

    init {
        initialize()
        addListener("chatTabs") {
            ChatTabs.initialize()
            if (!chatTabs) {
                val dummy =
                    ChatTab(
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
                ChatTabs.currentTab = dummy
            } else {
                ChatTabs.currentTab = ChatTabs.tabs[0]
            }
        }
        addListener("chatShortcuts") {
            ChatShortcuts.initialize()
        }
        // addDependency("showTimestampHover", "showTimestamp")
    }
}
