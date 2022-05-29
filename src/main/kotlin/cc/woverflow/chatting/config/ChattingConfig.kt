package cc.woverflow.chatting.config

import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.chat.ChatShortcuts
import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.gui.ChatShortcutViewGui
import cc.woverflow.chatting.gui.components.TabButton
import cc.woverflow.onecore.utils.openScreen
import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import java.awt.Color
import java.io.File

object ChattingConfig :
    Vigilant(
        File(Chatting.modDir, "${Chatting.ID}.toml"),
        Chatting.NAME,
        sortingBehavior = ConfigSorting
    ) {

    @Property(
        type = PropertyType.SELECTOR,
        name = "Text Render Type",
        description = "Choose the type of rendering for the text.",
        category = "General",
        options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var textRenderType = 1

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Tooltip Background",
        description = "Remove the tooltip background.",
        category = "General"
    )
    var removeTooltipBackground = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Scroll Bar",
        description = "Remove the scroll bar.",
        category = "General"
    )
    var removeScrollBar = false

    @Property(
        type = PropertyType.COLOR,
        name = "Chat Background Color",
        description = "Change the color of the chat background.",
        category = "General",
        allowAlpha = false
    )
    var chatBackgroundColor = Color(0, 0, 0, 128)

    @Property(
        type = PropertyType.COLOR,
        name = "Copy Chat Message Background Color",
        description = "Change the color of chat messages that are ready to copy.",
        category = "General",
        allowAlpha = false
    )
    var hoveredChatBackgroundColor = Color(80, 80, 80, 128)

    @Property(
        type = PropertyType.SWITCH,
        name = "Compact Input Box",
        description = "Make the width of the input box the same size as the chat box.",
        category = "General"
    )
    var compactInputBox = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Inform for Alternatives",
        description =
            "Inform the user if a mod they are using can be replaced by a feature in Chatting.",
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

    @Property(
        type = PropertyType.SLIDER,
        min = 80,
        max = 100,
        name = "Spam Blocker Threshold",
        description =
            "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.\n" +
                "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at.\n" +
                "Note that this is not and never will be 100% accurate; however, it's pretty much guaranteed to block most spam.",
        category = "Player Chats"
    )
    var spamThreshold = 100

    @Property(
        type = PropertyType.SWITCH,
        name = "Custom Chat Formatting",
        description =
            "Reformat all Skyblock chat messages. Example:\n" +
                "§a[VIP] Person§f: Message\n§7Person2: Message\n" +
                "§eBecomes:\n" +
                "§aPerson§f: Message\n§7Person2§f: Message",
        category = "Player Chats"
    )
    var customChatFormatting = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Spam",
        description =
            "When Chatting detects spam (if it's enabled), hide it instead of just graying it out.",
        category = "Player Chats"
    )
    var hideSpam = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Custom Chat Height",
        description = "Allows you to change the height of chat to heights greater than before.",
        category = "Chat Window"
    )
    var customChatHeight = true

    @Property(
        type = PropertyType.SLIDER,
        min = 180,
        max = 2160,
        name = "Focused Height",
        description = "Height in pixels.",
        category = "Chat Window"
    )
    var focusedHeight = 180

    @Property(
        type = PropertyType.SLIDER,
        min = 180,
        max = 2160,
        name = "Unfocused Height",
        description = "Height in pixels.",
        category = "Chat Window"
    )
    var unfocusedHeight = 180

    @Property(
        type = PropertyType.SELECTOR,
        name = "Screenshot Mode",
        description = "The mode in which screenshotting will work.",
        category = "Screenshotting",
        options = ["Save To System", "Add To Clipboard", "Both"]
    )
    var copyMode = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Searching",
        description = "Add a chat search bar.",
        category = "Searching"
    )
    var chatSearch = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Tabs",
        description = "Add chat tabs.",
        category = "Tabs"
    )
    var chatTabs = true
        get() {
            if (!field) return false
            return if (hypixelOnlyChatTabs) {
                EssentialAPI.getMinecraftUtil().isHypixel()
            } else {
                true
            }
        }

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Tabs Only on Hypixel",
        description = "Enable chat tabs only in Hypixel.",
        category = "Tabs"
    )
    var hypixelOnlyChatTabs = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Shortcuts",
        description = "Add chat shortcuts.",
        category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) {
                EssentialAPI.getMinecraftUtil().isHypixel()
            } else {
                true
            }
        }

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Shortcuts Only on Hypixel",
        description = "Enable chat shortcuts only in Hypixel.",
        category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Edit Chat Shortcuts",
        description = "Edit chat shortcuts.",
        category = "Shortcuts"
    )
    fun openChatShortcutsGUI() = ChatShortcutViewGui().openScreen()

    init {
        initialize()
        registerListener("chatTabs") { funny: Boolean ->
            chatTabs = funny
            ChatTabs.initialize()
            if (!funny) {
                val dummy =
                    ChatTab(
                        true,
                        "ALL",
                        false,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        TabButton.color,
                        TabButton.hoveredColor,
                        TabButton.selectedColor,
                        ""
                    )
                dummy.initialize()
                ChatTabs.currentTab = dummy
            } else {
                ChatTabs.currentTab = ChatTabs.tabs[0]
            }
        }
        registerListener("chatShortcuts") { funny: Boolean ->
            chatShortcuts = funny
            ChatShortcuts.initialize()
        }
        // addDependency("showTimestampHover", "showTimestamp")
    }

    private object ConfigSorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> = Comparator { o1, o2 ->
            if (o1.name == "General") return@Comparator -1
            if (o2.name == "General") return@Comparator 1 else compareValuesBy(o1, o2) { it.name }
        }
    }
}
