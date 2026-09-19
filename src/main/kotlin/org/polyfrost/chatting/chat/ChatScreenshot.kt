package org.polyfrost.chatting.chat

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.ChatComponentText
import net.ornithemc.osl.keybinds.api.KeybindEvents
import net.ornithemc.osl.keybinds.api.KeybindRegistry
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents
import org.lwjgl.BufferUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.polyfrost.chatting.chat.ChatBackground
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

/** Native 1.8.9 framebuffer capture for the chat currently visible to the player. */
object ChatScreenshot {
    private val keybind = KeyBinding("key.chatting.screenshot", Keyboard.KEY_NONE, "category.chatting")
    private val fileFormatter = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS'.png'", Locale.ROOT)
    private val formatting = Regex("§[0-9a-fA-F]")
    private var initialized = false
    private var screenshotKeyWasDown = false

    @JvmStatic
    fun initialize() {
        if (initialized) return
        initialized = true
        KeybindEvents.REGISTER_KEYBINDS.register { KeybindRegistry.register(keybind) }
        MinecraftClientEvents.TICK_END.register {
            val isDown = keybind.isKeyDown
            if (isDown && !screenshotKeyWasDown) capture()
            screenshotKeyWasDown = isDown
        }
    }

    /** Enables the AWT clipboard where the platform can safely provide it. */
    @JvmStatic
    fun allowAwtClipboard() {
        // Creating AWT's toolkit crashes the macOS game client.
        if (System.getProperty("os.name", "").lowercase(Locale.ROOT).contains("mac")) return
        if (System.getProperty("java.awt.headless").equals("true", ignoreCase = true)) {
            System.clearProperty("java.awt.headless")
        }
    }

    @JvmStatic
    fun capture() {
        val mc = Minecraft.getMinecraft()
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (mc.currentScreen != null && mc.currentScreen !is GuiChat) return
        if (!OpenGlHelper.isFramebufferEnabled()) {
            message("Chat screenshot needs framebuffer support.")
            return
        }

        val accessor = mc.ingameGUI.chatGUI as GuiNewChatAccessor
        val lines = ChatSearchingManager.filterMessages(ChatSearchingManager.lastSearch, accessor.drawnChatLines)
            ?.let { visibleLines(it, accessor.scrollPos, mc.ingameGUI.chatGUI.lineCount) }
            .orEmpty()
        if (lines.isEmpty()) {
            message("Chat window is empty.")
            return
        }

        val image = render(lines) ?: return
        val save = ChattingConfig.copyMode != 1
        val copy = ChattingConfig.copyMode != 0
        val file = File(mc.mcDataDir, "screenshots/chat/${fileFormatter.format(Date())}")
        val saved = !save || runCatching {
            file.parentFile?.mkdirs()
            ImageIO.write(image, "png", file)
        }.isSuccess
        val copied = !copy || runCatching {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(ImageTransferable(image), null)
        }.isSuccess

        when {
            !saved && !copied -> message("Could not save or copy the chat screenshot.")
            !saved -> message("Chat screenshot copied, but could not be saved.")
            !copied -> message("Chat screenshot saved to screenshots/chat, but clipboard is unavailable.")
            save && copy -> message("Chat screenshot saved to screenshots/chat and copied to the clipboard.")
            save -> message("Chat screenshot saved to screenshots/chat.")
            else -> message("Chat screenshot copied to the clipboard.")
        }
    }

    private fun visibleLines(lines: List<ChatLine>, scrollPos: Int, lineCount: Int): List<ChatLine> {
        val first = scrollPos.coerceIn(0, lines.size)
        val last = min(lines.size, first + max(0, lineCount))
        return lines.subList(first, last)
    }

    private fun render(lines: List<ChatLine>): BufferedImage? {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj
        val texts = lines.map { it.chatComponent.formattedText }
        val style = ScreenshotStyle.current()
        val margin = if (style.border) 1 else 0
        val width = texts.maxOf(font::getStringWidth).coerceAtLeast(1) + margin * 2
        val height = texts.size * 9 + margin * 2
        val framebuffer = Framebuffer(width * 2, height * 2, false)
        return try {
            framebuffer.framebufferColor[0] = 0f
            framebuffer.framebufferColor[1] = 0f
            framebuffer.framebufferColor[2] = 0f
            framebuffer.framebufferColor[3] = 0f
            framebuffer.framebufferClear()
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.loadIdentity()
            GlStateManager.ortho(0.0, (width * 2).toDouble(), (height * 2).toDouble(), 0.0, 1000.0, 3000.0)
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.loadIdentity()
            GlStateManager.translate(0f, 0f, -2000f)
            framebuffer.bindFramebuffer(true)
            GlStateManager.scale(2f, 2f, 1f)
            if (style.background) {
                val opacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f
                net.minecraft.client.gui.Gui.drawRect(0, 0, width, height, ChatBackground.tint((opacity * 127.5f).toInt() shl 24))
            }
            texts.forEachIndexed { index, text ->
                val x = margin
                val y = (texts.size - 1 - index) * 9 + margin
                if (style.border) {
                    val outline = blackOut(text)
                    for (offsetY in -1..1) for (offsetX in -1..1) {
                        if (offsetX != 0 || offsetY != 0) font.drawString(outline, x + offsetX, y + offsetY, 0xFF000000.toInt())
                    }
                }
                if (style.shadow) font.drawStringWithShadow(text, x.toFloat(), y.toFloat(), 0xFFFFFFFF.toInt())
                else font.drawString(text, x, y, 0xFFFFFFFF.toInt())
            }
            read(framebuffer)
        } catch (_: Throwable) {
            message("Could not capture the chat screenshot.")
            null
        } finally {
            framebuffer.deleteFramebuffer()
            mc.entityRenderer.setupOverlayRendering()
            mc.framebuffer.bindFramebuffer(true)
        }
    }

    private fun read(framebuffer: Framebuffer): BufferedImage {
        val width = framebuffer.framebufferWidth
        val height = framebuffer.framebufferHeight
        val textureWidth = framebuffer.framebufferTextureWidth
        val textureHeight = framebuffer.framebufferTextureHeight
        val values = IntArray(textureWidth * textureHeight)
        val pixels = BufferUtils.createIntBuffer(values.size)
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        GlStateManager.bindTexture(framebuffer.framebufferTexture)
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels)
        pixels.get(values)
        TextureUtil.processPixelValues(values, textureWidth, textureHeight)
        return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also { image ->
            val verticalOffset = textureHeight - height
            for (y in 0 until height) {
                image.setRGB(0, y, width, 1, values, (verticalOffset + y) * textureWidth, textureWidth)
            }
        }
    }

    private fun message(message: String) {
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(ChatComponentText("§7[§bChatting§7] $message"))
    }

    private fun blackOut(text: String) = text
        .replace(formatting, "§0")
        .replace("§r", "§0")

    private data class ScreenshotStyle(val shadow: Boolean, val background: Boolean, val border: Boolean) {
        companion object {
            fun current(): ScreenshotStyle {
                val shadow = ChattingConfig.textRenderType == 1 || ChattingConfig.screenshotForceShadow
                val background = ChattingConfig.screenshotBackground
                return ScreenshotStyle(shadow, background, ChattingConfig.screenshotBorder && !shadow && !background)
            }
        }
    }

    private class ImageTransferable(private val image: Image) : Transferable {
        override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
        override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
        override fun getTransferData(flavor: DataFlavor): Any {
            if (!isDataFlavorSupported(flavor)) throw java.awt.datatransfer.UnsupportedFlavorException(flavor)
            return image
        }
    }
}
