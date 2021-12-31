package com.raeids.stratus.gui

import com.raeids.stratus.chat.ChatShortcuts
import com.raeids.stratus.gui.components.TextBlock
import gg.essential.api.EssentialAPI
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.RelativeWindowConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.settings.ButtonComponent

class ChatShortcutViewGui : WindowScreen(version = ElementaVersion.V1) {
    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        for ((yes, shortcut) in ChatShortcuts.shortcuts.withIndex()) {
            val block = UIBlock(VigilancePalette.getBackground()).constrain {
                x = 3.percent()
                y = (yes * 12).percent()
                this.width = 94.percent()
                this.height = 25.pixels()
            } childOf this.window
            TextBlock(shortcut.first).constrain {
                x = RelativeWindowConstraint(0.05F)
                y = CenterConstraint()
            } childOf block
            TextBlock(shortcut.second).constrain {
                x = SiblingConstraint(10F)
                y = CenterConstraint()
            } childOf block
            ButtonComponent("Edit") {
                println("${shortcut.first} ${shortcut.second}")
                EssentialAPI.getGuiUtil().openScreen(ChatShortcutEditGui(shortcut.first, shortcut.second, true))
            } constrain {
                x = SiblingConstraint(20F)
                y = CenterConstraint()
            } childOf block
            ButtonComponent("Delete") {
                println("${shortcut.first} ${shortcut.second}")
                ChatShortcuts.removeShortcut(shortcut.first)
                EssentialAPI.getGuiUtil().openScreen(ChatShortcutViewGui())
            } constrain {
                x = SiblingConstraint(5F)
                y = CenterConstraint()
            } childOf block
        }
        ButtonComponent("New") {
            EssentialAPI.getGuiUtil().openScreen(ChatShortcutEditGui("", "", false))
        } constrain {
            x = CenterConstraint()
            y = 80.percent()
        } childOf window
    }
}