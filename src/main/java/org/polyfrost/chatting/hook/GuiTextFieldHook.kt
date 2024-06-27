package org.polyfrost.chatting.hook

import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.getAlpha
import org.polyfrost.chatting.config.ChattingConfig.chatInput
import org.polyfrost.chatting.utils.ModCompatHooks

object GuiTextFieldHook {
    @JvmStatic
    fun redirectDrawString(text: String, x: Float, y: Float, color: Int): Int {
        return when (chatInput.inputTextRenderType) {
            0 -> Platform.getGLPlatform().drawText(text, x, y, color, false).toInt()
            1 -> Platform.getGLPlatform().drawText(text, x, y, color, true).toInt()
            2 -> TextRenderer.drawBorderedText(text, x, y, color, color.getAlpha())
            else -> ModCompatHooks.fontRenderer.drawString(text, x, y, color, true)
        }
    }
}