package com.raeids.stratus.hook

import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.ScaledResolution

var inputField: GuiTextField? = null
var sr: ScaledResolution? = null
var prevText = ""

fun initGui() {
    sr = ScaledResolution(Minecraft.getMinecraft())
    inputField = GuiTextField(
        694209000,
        Minecraft.getMinecraft().fontRendererObj,
        sr!!.scaledWidth * 4 / 5 - 1,
        sr!!.scaledHeight - 13,
        sr!!.scaledWidth / 5,
        12
    )
    inputField!!.maxStringLength = 100
    inputField!!.enableBackgroundDrawing = true
    inputField!!.isFocused = false
    inputField!!.text = ""
    inputField!!.setCanLoseFocus(true)
    prevText = ""
}

fun updateScreen() {
    inputField?.updateCursorCounter()
}

fun filterMessages(list: List<ChatLine?>?): List<ChatLine?>? {
    if (inputField == null || list == null || inputField?.text.isNullOrBlank()) return list
    return list.filter {
        it != null && UTextComponent.stripFormatting(it.chatComponent.unformattedText).lowercase()
            .contains(inputField!!.text!!.lowercase())
    }
}