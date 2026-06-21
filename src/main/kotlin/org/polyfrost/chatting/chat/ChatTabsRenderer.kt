package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import org.polyfrost.chatting.config.ChattingConfig
//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics*/
//?} else {
import net.minecraft.client.gui.GuiGraphics
//?}

object ChatTabsRenderer {

    data class View(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val name: String,
        val backgroundColor: Int,
        val textColor: Int,
        val textX: Int,
        val textY: Int,
    )

    private const val HEIGHT = 12

    fun isActive(): Boolean = ChattingConfig.chatTabs && ChatTabs.tabs.isNotEmpty()

    fun views(mouseX: Int, mouseY: Int): List<View> {
        if (!isActive()) return emptyList()
        val mc = Minecraft.getInstance()
        val font = mc.font
        val y = mc.window.guiScaledHeight - 26
        val result = ArrayList<View>(ChatTabs.tabs.size)
        var cursor = 4
        for (tab in ChatTabs.tabs) {
            val textWidth = font.width(tab.name)
            val boxX = cursor - 2
            val boxW = textWidth + 4
            val hovered = mouseX >= boxX && mouseX < boxX + boxW && mouseY >= y && mouseY < y + HEIGHT
            result.add(
                View(
                    x = boxX,
                    y = y,
                    width = boxW,
                    height = HEIGHT,
                    name = tab.name,
                    backgroundColor = backgroundColor(hovered),
                    textColor = textColor(tab, hovered),
                    textX = boxX + (boxW - textWidth) / 2,
                    textY = y + (HEIGHT - 8) / 2,
                )
            )
            cursor += 6 + textWidth
        }
        return result
    }

    fun shadow(): Boolean = ChattingConfig.buttonShadow

    fun draw(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val font = Minecraft.getInstance().font
        val shadow = shadow()
        for (v in views(mouseX, mouseY)) {
            graphics.fill(v.x, v.y, v.x + v.width, v.y + v.height, v.backgroundColor)
            //? if >=26 {
            /*graphics.text(font, v.name, v.textX, v.textY, v.textColor, shadow)*/
            //?} else {
            graphics.drawString(font, v.name, v.textX, v.textY, v.textColor, shadow)
            //?}
        }
    }

    fun click(mouseX: Double, mouseY: Double, shift: Boolean): Boolean {
        if (!isActive()) return false
        val mc = Minecraft.getInstance()
        val font = mc.font
        val y = mc.window.guiScaledHeight - 26
        var cursor = 4
        for (tab in ChatTabs.tabs) {
            val textWidth = font.width(tab.name)
            val boxX = cursor - 2
            val boxW = textWidth + 4
            if (mouseX >= boxX && mouseX < boxX + boxW && mouseY >= y && mouseY < y + HEIGHT) {
                ChatTabs.click(tab, shift)
                return true
            }
            cursor += 6 + textWidth
        }
        return false
    }

    private fun backgroundColor(hovered: Boolean): Int =
        if (hovered) ChattingConfig.chatButtonHoveredBackgroundColor.argb
        else ChattingConfig.chatButtonBackgroundColor.argb

    private fun textColor(tab: ChatTab, hovered: Boolean): Int {
        val selected = ChatTabs.isSelected(tab)
        val rgb = when {
            !selected -> tab.selectedColor ?: ChatTab.SELECTED_COLOR
            hovered -> tab.hoveredColor ?: ChatTab.HOVERED_COLOR
            else -> tab.color ?: ChatTab.COLOR
        }
        //force alhpha proprekrly
        return if (rgb and -0x1000000 == 0) rgb or -0x1000000 else rgb
    }
}
