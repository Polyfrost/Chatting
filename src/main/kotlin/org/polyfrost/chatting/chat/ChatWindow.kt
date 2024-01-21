package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.config.annotations.DualOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.chatting.config.ChattingConfig

class ChatWindow : Hud(true) {

    @Exclude
    private val exampleChat = listOf(
        "Chatting by PolyFrost Team",
        "----------------------------------------",
        "This is a movable chat",
        "Drag me around!",
        "Click to drag"
    )

    @DualOption(
        name = "Type",
        left = "Left",
        right = "Right"
    )
    var chatType = false

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (!example) return
        nanoVG(true) {
            drawRect(x, y, position.width, position.height, ChattingConfig.chatBackgroundColor.rgb, false)
        }
        var textY = y
        for (text in exampleChat) {
            TextRenderer.drawScaledString(text, x, textY, 16777215, TextRenderer.TextType.toType(ChattingConfig.textRenderType), scale)
            textY += 9 * scale
        }
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        return 240f * scale
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        return 9f * 5 * scale
    }

}