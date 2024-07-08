package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UResolution
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.chat.ChatHooks.inputBoxRight
import org.polyfrost.chatting.utils.ModCompatHooks

class ChatInputBox : BasicHud(true, -100f, -100f) {

    init {
        scale = 1f
        paddingX = 0f
        paddingY = 0f
    }

    @Switch(
        name = "Compact Input Box",
        description = "Make the chat input box the same width as the chat box."
    )
    var compactInputBox = false

    @Switch(
        name = "Input Field Draft",
        description = "Drafts the text you wrote in the input field after closing the chat and backs it up when opening the chat again."
    )
    var inputFieldDraft = false

    fun drawBG() {
        if (!ModCompatHooks.shouldDrawInputBox) return
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        val scale = UResolution.scaleFactor.toFloat()
        drawBackground(2f, UResolution.scaledHeight - 14f + (if (UResolution.windowHeight % 2 == 1) scale - 1 else 0f) / scale, inputBoxRight - 2f, 12f, 1f)
        GlStateManager.disableBlend()
        GlStateManager.disableAlpha()
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
    }

    override fun shouldShow(): Boolean {
        return false
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        return 0f
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        return 0f
    }

    fun setBackground(boolean: Boolean) {
        background = boolean
    }
}