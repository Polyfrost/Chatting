package cc.woverflow.chattils.gui

import cc.woverflow.chattils.chat.ChatShortcuts
import gg.essential.api.EssentialAPI
import gg.essential.api.gui.buildConfirmationModal
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.settings.ButtonComponent
import gg.essential.vigilance.gui.settings.TextComponent

class ChatShortcutEditGui(private var alias: String, private var command: String, private val editing: Boolean) :
    WindowScreen(restoreCurrentGuiOnClose = true, version = ElementaVersion.V1) {

    private val initialAlias = alias
    private val initialCommand = command

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        val block = UIBlock(VigilancePalette.getBackground()).constrain {
            this.x = CenterConstraint()
            this.y = CenterConstraint()
            this.width = 100.pixels()
            this.height = 100.pixels()
        } childOf window
        TextComponent(initialAlias, "Alias", wrap = false, protected = false).constrain {
            x = CenterConstraint()
            y = 10.percent()
        }.childOf(block).onValueChange {
            if (it is String) alias = it
        }
        TextComponent(initialCommand, "Command", wrap = false, protected = false).constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
        }.childOf(block).onValueChange {
            if (it is String) command = it
        }
        if (editing) {
            ButtonComponent("Reset") {
                EssentialAPI.getGuiUtil().openScreen(ChatShortcutEditGui(initialAlias, initialCommand, editing))
            } constrain {
                x = CenterConstraint()
                y = 70.percent()
            } childOf window
        }
        ButtonComponent("Save") {
            alias = alias.substringAfter("/")
            command = command.substringAfter("/")
            if (editing) {
                ChatShortcuts.removeShortcut(initialAlias)
            }
            if (alias.isBlank() || command.isBlank()) {
                return@ButtonComponent
            }
            if (ChatShortcuts.shortcuts.any { it.first == alias }) {
                EssentialAPI.getGuiUtil().openScreen(ChatShortcutConfirmGui(alias, command))
                return@ButtonComponent
            }
            ChatShortcuts.writeShortcut(alias, command)
            restorePreviousScreen()
        } constrain {
            x = CenterConstraint()
            y = 80.percent()
        } childOf window
    }

    inner class ChatShortcutConfirmGui(private var alias: String, private var command: String) :
        WindowScreen(restoreCurrentGuiOnClose = true, version = ElementaVersion.V1) {
        override fun initScreen(width: Int, height: Int) {
            super.initScreen(width, height)
            EssentialAPI.getEssentialComponentFactory().buildConfirmationModal {
                text = "An alias with this name already exists, are you sure you want to overwrite it?"
                onConfirm = {
                    ChatShortcuts.writeShortcut(alias, command)
                    EssentialAPI.getGuiUtil().openScreen(null)
                }
                onDeny = {
                    restorePreviousScreen()
                }
            } childOf this@ChatShortcutConfirmGui.window
        }
    }
}
