package org.polyfrost.chatting.hud

//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
//?} else {
/*import net.minecraft.client.gui.GuiGraphics
*///?}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.hud.v1.Section
import org.polyfrost.chatting.config.ChattingConfig
import kotlin.math.ceil

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

    override fun update(): Boolean {
        tickPosition(this)
        return false
    }

    override fun hasBackground() = false

    override fun multipleInstancesAllowed() = false

    override fun deletable() = false

    override fun defaultPosition(): Pair<Float, Float> = DEFAULT_LEFT to defaultTop()

    override fun setup() {
        val onReset = Runnable { onPositionReset() }
        addCallback("section", onReset)
        addCallback("relativeX", onReset)
        addCallback("relativeY", onReset)
    }

    override fun render(mcCtx: GuiGraphics) {
        if (!HudManager.isEditing) return
        tickPosition(this)
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

        // Vanilla draws the chat background from x=-4 to maxWidth+8 in chat space, then
        // translates by +4 and scales by the chat-scale option, so on screen it occupies
        // [0, (maxWidth + 12) * scale]. maxWidth is getWidth() divided by that scale.
        private fun chatWidth(): Float {
            val scale = chatScaleOption()
            val maxWidth = ceil(ChatComponent.getWidth(mc().options.chatWidth().get()) / scale)
            return (maxWidth + 12) * scale
        }

        private fun chatHeight(): Float =
            ChatComponent.getHeight(mc().options.chatHeightUnfocused().get()) * chatScaleOption()

        /** Y of the vanilla chat's top-left by default */
        private fun defaultTop(): Float =
            mc().window.guiScaledHeight - BOTTOM_MARGIN - chatHeight()

        private var hasBaseline = false
        private var baseSection: Section? = null
        private var baseRelX = 0f
        private var baseRelY = 0f

        /**
         * Pins the HUD's stored position to the live vanilla chat anchor while the user hasn't moved
         * it, so the HUD editor's box tracks where the chat actually renders across window resizes and
         * GUI-scale changes (rather than a resolution-frozen snapshot). A position change we didn't
         * make ourselves — an editor drag — flips [ChattingConfig.chatWindowMoved] and hands control
         * over to the stored position. Our own writes are recorded as the [baseSection]/[baseRelX]/
         * [baseRelY] baseline so they aren't mistaken for a move.
         */
        private fun tickPosition(hud: ChatWindowHud) {
            if (ChattingConfig.chatWindowMoved) {
                hasBaseline = false
                return
            }
            if (hasBaseline &&
                (hud.section != baseSection ||
                    hud.relativeX != baseRelX ||
                    hud.relativeY != baseRelY)
            ) {
                ChattingConfig.chatWindowMoved = true
                ChattingConfig.save()
                hasBaseline = false
                return
            }
            hud.setAbsolutePosition(DEFAULT_LEFT, defaultTop())
            baseSection = hud.section
            baseRelX = hud.relativeX
            baseRelY = hud.relativeY
            hasBaseline = true
        }

        /**
         * Fired when the position is reset to default from the HUD editor (see [setup]). Returns the
         * chat to the vanilla-tracking state so "reset to default" matches the real vanilla position
         * instead of the frozen snapshot captured at load.
         */
        private fun onPositionReset() {
            if (ChattingConfig.chatWindowMoved) {
                ChattingConfig.chatWindowMoved = false
                ChattingConfig.save()
            }
            hasBaseline = false
        }

        @JvmStatic
        fun isActive(): Boolean {
            val hud = instance ?: return false
            if (hud.hidden) return false
            return HudManager.isEditing || ChattingConfig.chatWindowMoved
        }

        /**
         * Mirrors OneConfig's per-HUD visibility gating (see `HudManager.render`) for the chat, which
         * renders through the vanilla [ChatComponent] and so never passes through that gate itself.
         * The chat screen is exempt from the "Show in GUIs" rule so opening the input to type doesn't
         * hide the history.
         */
        @JvmStatic
        fun shouldHideForVisibility(chatFocused: Boolean): Boolean {
            val hud = instance ?: return false
            if (HudManager.isEditing) return false
            if (HudManager.isDebugScreenVisible && !hud.showInF3) return true
            if (HudManager.isTabListVisible && !hud.showInTab) return true
            if (HudManager.isGuiScreenOpen && !chatFocused && !hud.showInScreens) return true
            return false
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
