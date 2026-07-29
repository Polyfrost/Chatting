package org.polyfrost.chatting.hud

//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
//?} else {
/*import net.minecraft.client.gui.GuiGraphics
*///?}
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.hud.v1.Section
import org.polyfrost.chatting.chat.ChatDimensions
import org.polyfrost.chatting.config.ChattingConfig
import kotlin.math.ceil

class ChatWindowHud : LegacyHud(
    id = "chat_window.json",
    title = "Chat Window",
    category = Category.INFO,
) {

    init {
        instance = this
        locked = true
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

    override fun showByDefault() = true

    override fun defaultPosition(): Pair<Float, Float> = DEFAULT_LEFT to defaultTop()

    override fun setup() {
        migrateLockDefault()
        hasBaseline = false
        val onReset = Runnable { if (isReal) onPositionReset() }
        addCallback("section", onReset)
        addCallback("relativeX", onReset)
        addCallback("relativeY", onReset)
    }

    private fun migrateLockDefault() {
        if (ChattingConfig.chatWindowLockMigrated) return
        ChattingConfig.chatWindowLockMigrated = true
        ChattingConfig.save()
        if (ChattingConfig.chatWindowMoved || locked) return
        locked = true
        save()
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

        private const val BOTTOM_MARGIN = 40

        private fun mc() = Minecraft.getInstance()

        private fun chatScaleOption(): Float = mc().options.chatScale().get().toFloat()

        private fun chatWidth(): Float {
            val scale = chatScaleOption()
            val maxWidth = ceil(ChatDimensions.width() / scale)
            val configuredWidth = (maxWidth + 12) * scale
            return capToAvailableSpace(configuredWidth, mc().window.guiScaledWidth)
        }

        private fun chatHeight(): Float {
            val configuredHeight = ChatDimensions.height(focused = false) * chatScaleOption()
            val availableHeight = mc().window.guiScaledHeight - BOTTOM_MARGIN
            return capToAvailableSpace(configuredHeight, availableHeight)
        }

        private fun capToAvailableSpace(configuredSize: Float, availableSize: Int): Float =
            if (availableSize > 0) configuredSize.coerceAtMost(availableSize.toFloat()) else configuredSize

        private fun defaultTop(): Float =
            mc().window.guiScaledHeight - BOTTOM_MARGIN - chatHeight()

        private var hasBaseline = false
        private var baseSection: Section? = null
        private var baseRelX = 0f
        private var baseRelY = 0f

        /** syncs to the vanilla position unless the user has moved the chat window */
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
            hud.section = Section.BottomLeft
            hud.x = DEFAULT_LEFT
            hud.y = defaultTop()
            baseSection = hud.section
            baseRelX = hud.relativeX
            baseRelY = hud.relativeY
            hasBaseline = true
        }

        private fun onPositionReset() {
            if (ChattingConfig.chatWindowMoved) {
                ChattingConfig.chatWindowMoved = false
                ChattingConfig.save()
            }
            hasBaseline = false
        }

        private fun placed(): ChatWindowHud? = instance?.takeIf { it.isReal }

        @JvmStatic
        fun isActive(): Boolean {
            val hud = placed() ?: return false
            if (hud.hidden) return false
            return HudManager.isEditing || ChattingConfig.chatWindowMoved
        }

        @JvmStatic
        fun shouldHideForVisibility(chatFocused: Boolean): Boolean {
            val hud = placed() ?: return false
            if (HudManager.isEditing) return false
            if (HudManager.isDebugScreenVisible && !hud.showInF3) return true
            if (HudManager.isTabListVisible && !hud.showInTab) return true
            if (HudManager.isGuiScreenOpen && !chatFocused && !hud.showInScreens) return true
            return false
        }

        @JvmStatic
        fun chatScale(): Float = placed()?.effectiveScale ?: 1f

        @JvmStatic
        fun chatTranslateX(): Float = placed()?.x ?: DEFAULT_LEFT

        @JvmStatic
        fun chatTranslateY(): Float = placed()?.y ?: defaultTop()

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
