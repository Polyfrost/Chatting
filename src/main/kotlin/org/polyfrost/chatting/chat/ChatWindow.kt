package org.polyfrost.chatting.chat

import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.internal.hud.HudCore
import cc.polyfrost.oneconfig.libs.universal.UGraphics.GL
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.NanoVGHelper
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.setAlpha
import club.sk1er.patcher.config.PatcherConfig
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatComponentText
import org.polyfrost.chatting.Chatting
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.utils.EaseOutQuart
import org.polyfrost.chatting.utils.ModCompatHooks

class ChatWindow : BasicHud(true, 2f, 1080 - 27f - 45f - 12f,
    1f, true, true, 6f, 5f, 5f, OneColor(0, 0, 0, 120), false, 2f, OneColor(0, 0, 0)) {

    @Exclude
    private val exampleList: List<ChatLine> = listOf(
        ChatLine(0, ChatComponentText("§bChatting"), 0),
        ChatLine(0, ChatComponentText(""), 0),
        ChatLine(0, ChatComponentText("§aThis is a movable chat"), 0),
        ChatLine(0, ChatComponentText("§eDrag me around!"), 0),
        ChatLine(0, ChatComponentText("Click to drag"), 0)
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
    var previousAnimationWidth = 0f

    @Exclude
    var previousAnimationHeight = 0f

    @Exclude
    var isGuiIngame = false

    @Exclude
    var wasInChatGui = false

    var normalScale = 1f
    var lastChatGuiScale = -1f
    var transferOverScale = false

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

    @Switch(
        name = "Different Opacity When Open",
        description = "Change the opacity of the chat window when it is open."
    )
    var differentOpacity = false

    @Slider(
        min = 0F, max = 255F, name = "Open Background Opacity",
        description = "The opacity of the chat window when it is open."
    )
    var openOpacity = 120
        get() = field.coerceIn(0, 255)

    @Slider(
        min = 0F, max = 255F, name = "Open Border Opacity",
        description = "The opacity of the chat window border when it is open."
    )
    var openBorderOpacity = 255
        get() = field.coerceIn(0, 255)

    @Button(
        name = "Revert to Vanilla Chat Window",
        description = "Revert the chat window to the vanilla chat window, instead of the Chattings custom chat window.",
        text = "Revert"
    )
    var revertToVanilla = Runnable {
        rounded = false
        paddingX = 0f
        paddingY = 0f
        ChattingConfig.smoothBG = false
    }

    @Button(
        name = "Revert to Chatting Chat Window",
        description = "Revert the chat window to the Chatting custom chat window, instead of the vanilla chat window.",
        text = "Revert"
    )
    var revertToChatting = Runnable {
        rounded = true
        paddingX = 5f
        paddingY = 5f
        ChattingConfig.smoothBG = true
    }

    init {
        showInDebug = true
        ignoreCaching = true
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
        val nanoVGHelper = NanoVGHelper.INSTANCE
        val animatingOpacity = wasInChatGui && (ChattingConfig.smoothBG && (previousAnimationWidth != width || previousAnimationHeight != height))
        wasInChatGui = mc.currentScreen is GuiChat || animatingOpacity
        previousAnimationWidth = width
        previousAnimationHeight = height
        val bgOpacity = openOpacity
        val borderOpacity = openBorderOpacity
        val bgColor = bgColor.getRGB().setAlpha(if (differentOpacity && wasInChatGui) bgOpacity else bgColor.alpha)
        val borderColor = borderColor.getRGB().setAlpha(if (differentOpacity && wasInChatGui) borderOpacity else borderColor.alpha)
        nanoVGHelper.setupAndDraw(true) { vg: Long ->
            if (rounded) {
                nanoVGHelper.drawRoundedRect(vg, x, y, width, height, bgColor, cornerRadius * scale)
                if (border) nanoVGHelper.drawHollowRoundRect(
                    vg,
                    x - borderSize * scale,
                    y - borderSize * scale,
                    width + borderSize * scale,
                    height + borderSize * scale,
                    borderColor,
                    cornerRadius * scale,
                    borderSize * scale
                )
            } else {
                nanoVGHelper.drawRect(vg, x, y, width, height, bgColor)
                if (border) nanoVGHelper.drawHollowRoundRect(
                    vg,
                    x - borderSize * scale,
                    y - borderSize * scale,
                    width + borderSize * scale,
                    height + borderSize * scale,
                    borderColor,
                    0f,
                    borderSize * scale
                )
            }
        }
    }

    fun drawBG() {
        animationWidth = widthAnimation.get()
        animationHeight = heightAnimation.get()
        width = position.width + (if (mc.ingameGUI.chatGUI.chatOpen && !Chatting.peeking && ChattingConfig.extendBG) ModCompatHooks.chatButtonOffset else 0) * scale
        val heightEnd = if (height == 0) 0f else (height + paddingY * 2f) * scale
        val duration = ChattingConfig.bgDuration
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        if (width != widthAnimation.end) {
            if (ChattingConfig.smoothBG) {
                widthAnimation = EaseOutQuart(duration, animationWidth, width, false)
            } else {
                animationWidth = width
            }
        }
        if (heightEnd != heightAnimation.end) {
            if (ChattingConfig.smoothBG) {
                heightAnimation = EaseOutQuart(duration, animationHeight, heightEnd, false)
            } else {
                animationHeight = heightEnd
            }
        }
        if (animationHeight <= 0.3f || !background || HudCore.editing) return
        nanoVG(true) {
            val scale = UResolution.scaleFactor.toFloat()
            drawBackground(position.x, position.bottomY - animationHeight + (if (UResolution.windowHeight % 2 == 1) scale - 1 else 0f) / scale, animationWidth, animationHeight, this@ChatWindow.scale)
        }
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

    fun setBackground(boolean: Boolean) {
        background = boolean
    }

    fun getBackgroundColor(): OneColor {
        return bgColor
    }

    fun setBackgroundColor(color: OneColor) {
        bgColor = color
    }

    override fun setScale(scale: Float, example: Boolean) {
        super.setScale(scale, example)
        normalScale = scale
    }

    fun updateMCChatScale() {
        if (ChattingConfig.chatWindow.lastChatGuiScale != mc.gameSettings.chatScale) {
            ChattingConfig.chatWindow.lastChatGuiScale = mc.gameSettings.chatScale
            ChattingConfig.chatWindow.scale = ChattingConfig.chatWindow.normalScale * mc.gameSettings.chatScale
        }
    }

}