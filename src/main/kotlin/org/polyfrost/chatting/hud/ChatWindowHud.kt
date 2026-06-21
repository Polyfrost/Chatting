package org.polyfrost.chatting.hud

//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics*/
//?} else {
import net.minecraft.client.gui.GuiGraphics
//?}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.hud.v1.Section
import org.polyfrost.chatting.config.ChattingConfig

class ChatWindowHud : LegacyHud(
    id = "chat_window.json",
    title = "Chat Window",
    category = Category.INFO,
) {

    init {
        instance = this
    }

    override val width: Float get() = chatWidth()

    override val height: Float get() = chatHeight()

    override fun update() = false

    override fun hasBackground() = false

    override fun multipleInstancesAllowed() = false

    override fun defaultPosition(): Pair<Float, Float> = DEFAULT_LEFT to defaultTop()

    override fun render(mcCtx: GuiGraphics) {
        if (!HudManager.isEditing) return
        val w = width.toInt()
        val h = height.toInt()
        mcCtx.fill(0, 0, w, h, 0x40000000)
        val border = 0x80FFFFFF.toInt()
        mcCtx.fill(0, 0, w, 1, border)
        mcCtx.fill(0, h - 1, w, h, border)
        mcCtx.fill(0, 0, 1, h, border)
        mcCtx.fill(w - 1, 0, w, h, border)
    }

    companion object {
        @JvmStatic
        var instance: ChatWindowHud? = null
            private set

        private const val DEFAULT_LEFT = 0f

        // Vanilla anchors the chat bottom 40px above the bottom of the screen
        private const val BOTTOM_MARGIN = 40

        private fun mc() = Minecraft.getInstance()

        private fun chatScaleOption(): Float = mc().options.chatScale().get().toFloat()

        private fun chatWidth(): Float = ChatComponent.getWidth(mc().options.chatWidth().get()).toFloat()

        private fun chatHeight(): Float =
            ChatComponent.getHeight(mc().options.chatHeightUnfocused().get()) * chatScaleOption()

        /** Y of the vanilla chat's top-left by default */
        private fun defaultTop(): Float =
            mc().window.guiScaledHeight - BOTTOM_MARGIN - chatHeight()

        private var baselineKnown = false
        private var lastRelX = 0f
        private var lastRelY = 0f
        private var lastSection: Section? = null

        private fun detectMove(hud: ChatWindowHud) {
            if (!baselineKnown) {
                baselineKnown = true
            } else if (!ChattingConfig.chatWindowMoved &&
                (hud.section != lastSection ||
                    hud.relativeX != lastRelX ||
                    hud.relativeY != lastRelY)
            ) {
                ChattingConfig.chatWindowMoved = true
                ChattingConfig.save()
            }
            lastSection = hud.section
            lastRelX = hud.relativeX
            lastRelY = hud.relativeY
        }

        @JvmStatic
        fun isActive(): Boolean {
            val hud = instance ?: return false
            detectMove(hud)
            if (hud.hidden) return false
            return HudManager.isEditing || ChattingConfig.chatWindowMoved
        }

        @JvmStatic
        fun chatScale(): Float = instance?.effectiveScale ?: 1f

        @JvmStatic
        fun chatTranslateX(): Float = instance?.x ?: DEFAULT_LEFT

        @JvmStatic
        fun chatTranslateY(): Float = instance?.y ?: defaultTop()

        @JvmStatic
        fun anchorLeft(): Float = DEFAULT_LEFT

        @JvmStatic
        fun anchorTop(): Float = defaultTop()

        @JvmStatic
        fun mapMouseX(x: Double): Double {
            if (!isActive()) return x
            return DEFAULT_LEFT + (x - chatTranslateX()) / chatScale()
        }

        @JvmStatic
        fun mapMouseY(y: Double): Double {
            if (!isActive()) return y
            return anchorTop() + (y - chatTranslateY()) / chatScale()
        }
    }
}
