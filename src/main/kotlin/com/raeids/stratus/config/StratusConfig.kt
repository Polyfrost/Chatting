package com.raeids.stratus.config

import com.raeids.stratus.Stratus
import com.raeids.stratus.gui.ChatShortcutViewGui
import com.raeids.stratus.hook.ChatShortcuts
import com.raeids.stratus.hook.ChatTab
import com.raeids.stratus.hook.ChatTabs
import com.raeids.stratus.updater.DownloadGui
import com.raeids.stratus.updater.Updater
import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object StratusConfig : Vigilant(File(Stratus.modDir, "${Stratus.ID}.toml"), Stratus.NAME) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Searching",
        description = "Add a chat search bar.",
        category = "Searching"
    )
    var chatSearch = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Screenshot Mode",
        description = "The mode in which screenshotting will work.",
        category = "Screenshotting",
        options = [
            "Save To System",
            "Add To Clipboard",
            "Both"
        ]
    )
    var copyMode = 0

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
    fun openChatShortcutsGUI() {
        EssentialAPI.getGuiUtil().openScreen(ChatShortcutViewGui())
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "Updater"
    )
    var showUpdate = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadGui()) else EssentialAPI.getNotifications()
            .push(
                Stratus.NAME,
                "No update had been detected at startup, and thus the update GUI has not been shown."
            )
    }

    init {
        initialize()
        registerListener("chatTabs") { funny: Boolean ->
            chatTabs = funny
            ChatTabs.initialize()
            if (!funny) {
                val dummy = ChatTab("ALL", false, null, null, null, null, null, "")
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
    }
}