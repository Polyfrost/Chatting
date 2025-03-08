package org.polyfrost.chatting.chat

import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.OmniResolution
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import net.minecraft.client.renderer.GlStateManager
import org.polyfrost.chatting.chat.ChatHooks.inputBoxRight
import org.polyfrost.chatting.utils.ModCompatHooks
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud

class ChatInputBox : LegacyHud() {

    @Switch(
        title = "Compact Input Box",
        description = "Make the chat input box the same width as the chat box."
    )
    var compactInputBox = false

    @Switch(
        title = "Input Field Draft",
        description = "Drafts the text you wrote in the input field after closing the chat and backs it up when opening the chat again."
    )
    var inputFieldDraft = false

    fun drawBG() {
        if (!ModCompatHooks.shouldDrawInputBox) return
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        val scale = OmniResolution.scaleFactor.toFloat()
        drawBackground(2f, OmniResolution.scaledHeight - 14f + (if (OmniResolution.viewportHeight % 2 == 1) scale - 1 else 0f) / scale, inputBoxRight - 2f, 12f, 1f)
        GlStateManager.disableBlend()
        GlStateManager.disableAlpha()
    }

    fun setBackground(boolean: Boolean) {
        background = boolean
    }

    override var height: Float
        get() = TODO("Not yet implemented")
        set(value) {}
    override var width: Float
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun category() = Category.INFO

    override fun render(stack: OmniMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        TODO("Not yet implemented")
    }

    override fun title() = "Chat Input Box"

    override fun update() = false
}