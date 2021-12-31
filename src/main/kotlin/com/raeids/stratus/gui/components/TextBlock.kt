package com.raeids.stratus.gui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.BasicState
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.settings.SettingComponent

/**
 * Heavily modified from Vigilance under LGPLv3 (modified to be just a text block)
 * https://github.com/Sk1erLLC/Vigilance/blob/master/LICENSE
 */
class TextBlock(
    text: String
) : SettingComponent() {
    private val textHolder = UIBlock() constrain {
        width = ChildBasedSizeConstraint() + 6.pixels()
        height = ChildBasedSizeConstraint() + 6.pixels()
        color = VigilancePalette.getDarkHighlight().toConstraint()
    } childOf this effect OutlineEffect(
        VigilancePalette.getDivider(),
        1f
    ).bindColor(BasicState(VigilancePalette.getDivider()))

    private val text: UIText = UIText(text) constrain {
        x = 3.pixels()
        y = 3.pixels()
    }

    init {
        this.text childOf textHolder

        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
    }
}