package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.internal.hud.HudCore
import cc.polyfrost.oneconfig.libs.universal.UGraphics.GL
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.utils.EaseOutQuart
import org.polyfrost.chatting.utils.ModCompatHooks

class ChatWindow : BasicHud(true, 2f, 1080 - 27f - 45f - 12f) {

    @Exclude
    private val exampleList: List<ChatLine> = listOf(
        ChatLine(0, ChatComponentText("§bChatting"), 1),
        ChatLine(0, ChatComponentText(""), 2),
        ChatLine(0, ChatComponentText("§aThis is a movable chat"), 3),
        ChatLine(0, ChatComponentText("§eDrag me around!"), 4),
        ChatLine(0, ChatComponentText("Click to drag"), 5)
    )

    @Exclude
    var widthAnimation: Animation = DummyAnimation(0f)

    @Exclude
    var heightAnimation: Animation = DummyAnimation(0f)

    @Exclude
    var width = 0f

    @Exclude
    var height = 0

    @Exclude
    var animationWidth = 0f

    @Exclude
    var animationHeight = 0f

    @Exclude
    var isGuiIngame = false

    @Switch(
        name = "Custom Chat Height",
        description = "Set a custom height for the chat window. Allows for more customization than the vanilla chat height options."
    )
    var customChatHeight = false

    @Slider(
        min = 20F, max = 2160F, name = "Focused Height (px)",
        description = "The height of the chat window when focused."
    )
    var focusedHeight = 180
        get() = field.coerceIn(20, 2160)

    @Slider(
        min = 20F, max = 2160F, name = "Unfocused Height (px)",
        description = "The height of the chat window when unfocused."
    )
    var unfocusedHeight = 90
        get() = field.coerceIn(20, 2160)

    @Switch(
        name = "Custom Chat Width",
        description = "Set a custom width for the chat window. Allows for more customization than the vanilla chat width options."
    )
    var customChatWidth = false

    @Slider(
        min = 20F, max = 2160F, name = "Custom Width (px)",
        description = "The width of the chat window when focused."
    )
    var customWidth = 320
        get() = field.coerceIn(20, 2160)

    init {
        showInDebug = true
    }


    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (!example) return
        nanoVG(true){
            GL.pushMatrix()
            GL.translate(x, y + scale, 0f)
            GL.scale(scale, scale, 1f)
            for (chat in exampleList) {
                ModCompatHooks.redirectDrawString(chat.chatComponent.formattedText, 0f, 0f, -1, chat, false)
                GL.translate(0f, 9f, 0f)
            }
            GL.popMatrix()
        }

    }

    override fun drawBackground(x: Float, y: Float, width: Float, height: Float, scale: Float) {
        if (Chatting.isPatcher && PatcherConfig.transparentChat) return
        super.drawBackground(x, y, width, height, scale)
    }

    fun drawBG() {
        animationWidth = widthAnimation.get()
        animationHeight = heightAnimation.get()
        width = position.width + (if (mc.ingameGUI.chatGUI.chatOpen && !Chatting.peaking) ModCompatHooks.chatButtonOffset else 0) * scale
        val heightEnd = if (height == 0) 0f else (height + paddingY * 2f) * scale
        val duration = ChattingConfig.bgDuration
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        if (width != widthAnimation.end) {
            if (ChattingConfig.smoothBG)
                widthAnimation = EaseOutQuart(duration, animationWidth, width, false)
            else
                animationWidth = width
        }
        if (heightEnd != heightAnimation.end) {
            if (ChattingConfig.smoothBG)
                heightAnimation = EaseOutQuart(duration, animationHeight, heightEnd, false)
            else
                animationHeight = heightEnd
        }
        if (animationHeight <= 0.3f || !background || HudCore.editing) return
        nanoVG(true) {
            drawBackground(position.x, position.bottomY - animationHeight, animationWidth, animationHeight, scale)
        }
        GlStateManager.disableBlend()
        GlStateManager.disableAlpha()
    }

    fun canShow(): Boolean {
        showInChat = true
        return isEnabled && (shouldShow() || Platform.getGuiPlatform().isInChat) && (isGuiIngame xor isCachingIgnored)
    }

    fun getPaddingX(): Float {
        return paddingX
    }

    fun getPaddingY(): Float {
        return paddingY
    }

    override fun shouldDrawBackground(): Boolean {
        return HudCore.editing
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        return ((if (customChatWidth) Chatting.getChatWidth() else GuiNewChat.calculateChatboxWidth(mc.gameSettings.chatWidth)) + 4 + ModCompatHooks.chatHeadOffset) * scale
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        return 9f * 5 * scale
    }

}