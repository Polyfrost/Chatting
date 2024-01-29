package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.utils.ModCompatHooks

class ChatInputBox: BasicHud(true, -100f, -100f) {

    init {
        paddingX = 0f
        paddingY = 0f
    }

    @Switch(
        name = "Compact Input Box", category = "Input Box",
        description = "Make the chat input box the same width as the chat box."
    )
    var compactInputBox = false

    @Switch(
        name = "Input Field Draft", category = "Input Box",
        description = "Drafts the text you wrote in the input field after closing the chat and back it up when opening the chat again."
    )
    var inputFieldDraft = false

    @Exclude
    var bgTop = 0f

    fun drawBG(x: Float, y: Float, width: Float, height: Float) {
        bgTop = y / scale + height + 2
        if (!ModCompatHooks.shouldDrawInputBox) return
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        nanoVG(true) {
            drawBackground(x, y, width * scale, height * scale, scale)
        }
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
}