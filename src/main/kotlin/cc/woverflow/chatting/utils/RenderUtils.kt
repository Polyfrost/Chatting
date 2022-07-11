@file:JvmName("RenderUtils")

package cc.woverflow.chatting.utils

import cc.polyfrost.oneconfig.utils.IOUtils
import cc.woverflow.chatting.config.ChattingConfig
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.apache.commons.lang3.SystemUtils
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import sun.awt.datatransfer.DataTransferer
import sun.awt.datatransfer.SunClipboard
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO

/**
 * Taken from https://github.com/Moulberry/HyChat
 */
fun createBindFramebuffer(w: Int, h: Int): Framebuffer {
    val framebuffer = Framebuffer(w, h, false)
    framebuffer.framebufferColor[0] = 0x36 / 255f
    framebuffer.framebufferColor[1] = 0x39 / 255f
    framebuffer.framebufferColor[2] = 0x3F / 255f
    framebuffer.framebufferClear()
    GlStateManager.matrixMode(5889)
    GlStateManager.loadIdentity()
    GlStateManager.ortho(0.0, w.toDouble(), h.toDouble(), 0.0, 1000.0, 3000.0)
    GlStateManager.matrixMode(5888)
    GlStateManager.loadIdentity()
    GlStateManager.translate(0.0f, 0.0f, -2000.0f)
    framebuffer.bindFramebuffer(true)
    return framebuffer
}

/**
 * Taken from https://github.com/Moulberry/HyChat
 * Modified so if not on Windows just in case it will switch it to RGB and remove the transparent background.
 */
fun BufferedImage.copyToClipboard() {
    if (SystemUtils.IS_OS_WINDOWS) {
        try {
            val width = this.width
            val height = this.height
            val hdrSize = 0x28
            val buffer: ByteBuffer = ByteBuffer.allocate(hdrSize + width * height * 4)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            //Header size
            buffer.putInt(hdrSize)
            //Width
            buffer.putInt(width)
            //Int32 biHeight;
            buffer.putInt(height)
            //Int16 biPlanes;
            buffer.put(1.toByte())
            buffer.put(0.toByte())
            //Int16 biBitCount;
            buffer.put(32.toByte())
            buffer.put(0.toByte())
            //Compression
            buffer.putInt(0)
            //Int32 biSizeImage;
            buffer.putInt(width * height * 4)
            buffer.putInt(0)
            buffer.putInt(0)
            buffer.putInt(0)
            buffer.putInt(0)

            //Image data
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val argb: Int = this.getRGB(x, height - y - 1)
                    if (argb shr 24 and 0xFF == 0) {
                        buffer.putInt(0x00000000)
                    } else {
                        buffer.putInt(argb)
                    }
                }
            }
            buffer.flip()
            val hdrSizev5 = 0x7C
            val bufferv5: ByteBuffer = ByteBuffer.allocate(hdrSizev5 + width * height * 4)
            bufferv5.order(ByteOrder.LITTLE_ENDIAN)
            //Header size
            bufferv5.putInt(hdrSizev5)
            //Width
            bufferv5.putInt(width)
            //Int32 biHeight;
            bufferv5.putInt(height)
            //Int16 biPlanes;
            bufferv5.put(1.toByte())
            bufferv5.put(0.toByte())
            //Int16 biBitCount;
            bufferv5.put(32.toByte())
            bufferv5.put(0.toByte())
            //Compression
            bufferv5.putInt(0)
            //Int32 biSizeImage;
            bufferv5.putInt(width * height * 4)
            bufferv5.putInt(0)
            bufferv5.putInt(0)
            bufferv5.putInt(0)
            bufferv5.putInt(0)
            bufferv5.order(ByteOrder.BIG_ENDIAN)
            bufferv5.putInt(-0x1000000)
            bufferv5.putInt(0x00FF0000)
            bufferv5.putInt(0x0000FF00)
            bufferv5.putInt(0x000000FF)
            bufferv5.order(ByteOrder.LITTLE_ENDIAN)

            //BGRs
            bufferv5.put(0x42.toByte())
            bufferv5.put(0x47.toByte())
            bufferv5.put(0x52.toByte())
            bufferv5.put(0x73.toByte())
            for (i in bufferv5.position() until hdrSizev5) {
                bufferv5.put(0.toByte())
            }

            //Image data
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val argb: Int = this.getRGB(x, height - y - 1)
                    val a = argb shr 24 and 0xFF
                    var r = argb shr 16 and 0xFF
                    var g = argb shr 8 and 0xFF
                    var b = argb and 0xFF
                    r = r * a / 0xFF
                    g = g * a / 0xFF
                    b = b * a / 0xFF
                    bufferv5.putInt(a shl 24 or (r shl 16) or (g shl 8) or b)
                }
            }
            bufferv5.flip()
            val clip = Toolkit.getDefaultToolkit().systemClipboard
            val dt = DataTransferer.getInstance()
            val f: Field = dt.javaClass.getDeclaredField("CF_DIB")
            f.isAccessible = true
            val format: Long = f.getLong(null)
            val openClipboard: Method = clip.javaClass.getDeclaredMethod("openClipboard", SunClipboard::class.java)
            openClipboard.isAccessible = true
            openClipboard.invoke(clip, clip)
            val publishClipboardData: Method = clip.javaClass.getDeclaredMethod(
                "publishClipboardData",
                Long::class.javaPrimitiveType,
                ByteArray::class.java
            )
            publishClipboardData.isAccessible = true
            val arr: ByteArray = buffer.array()
            publishClipboardData.invoke(clip, format, arr)
            val closeClipboard: Method = clip.javaClass.getDeclaredMethod("closeClipboard")
            closeClipboard.isAccessible = true
            closeClipboard.invoke(clip)
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val pixels: IntArray =
        this.getRGB(0, 0, this.width, this.height, null, 0, this.width)
    val newImage = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB)
    newImage.setRGB(0, 0, newImage.width, newImage.height, pixels, 0, newImage.width)

    try {
        IOUtils.copyImageToClipboard(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Taken from https://github.com/Moulberry/HyChat
 */
fun Framebuffer.screenshot(file: File): BufferedImage {
    val w = this.framebufferWidth
    val h = this.framebufferHeight
    val i = w * h
    val pixelBuffer = BufferUtils.createIntBuffer(i)
    val pixelValues = IntArray(i)
    GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
    GlStateManager.bindTexture(this.framebufferTexture)
    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer)
    pixelBuffer[pixelValues] //Load buffer into array
    TextureUtil.processPixelValues(pixelValues, w, h) //Flip vertically
    val bufferedimage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    val j = this.framebufferTextureHeight - this.framebufferHeight
    for (k in j until this.framebufferTextureHeight) {
        for (l in 0 until this.framebufferWidth) {
            bufferedimage.setRGB(l, k - j, pixelValues[k * this.framebufferTextureWidth + l])
        }
    }
    if (ChattingConfig.copyMode != 1) {
        try {
            file.parentFile.mkdirs()
            ImageIO.write(bufferedimage, "png", file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return bufferedimage
}
/*/
private val timePattern = Regex("\\[\\d+:\\d+:\\d+]")
private var lastLines = mutableListOf<ChatLine>()
fun timestampPre() {
    if (!ChattingConfig.showTimestampHover) return
    val drawnChatLines = (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines
    val chatLine = getChatLineOverMouse(UMouse.getTrueX().roundToInt(), UMouse.getTrueY().roundToInt())

    lastLines.clear()
    for (line in drawnChatLines) {
        val chatComponent = line.chatComponent.createCopy()
        val newline = ChatLine(line.updatedCounter, chatComponent, line.chatLineID)
        lastLines.add(newline)
    }

    drawnChatLines.map {
        if (it != chatLine) it.chatComponent.siblings.removeAll { itt ->
            timePattern.find(ChatColor.stripControlCodes(itt.unformattedText)!!) != null
        }
    }
}

fun timestampPost() {
    if (!ChattingConfig.showTimestampHover) return
    val drawnChatLines = (Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines
    drawnChatLines.clear()
    drawnChatLines.addAll(lastLines)
}

private fun getChatLineOverMouse(mouseX: Int, mouseY: Int): ChatLine? {
    val chat = Minecraft.getMinecraft().ingameGUI.chatGUI
    if (!chat.chatOpen) return null
    val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
    val i = scaledResolution.scaleFactor
    val f = chat.chatScale
    val j = MathHelper.floor_float((mouseX / i - 3).toFloat() / f)
    val k = MathHelper.floor_float((mouseY / i - 27).toFloat() / f)
    if (j < 0 || k < 0) return null
    val drawnChatLines = (chat as GuiNewChatAccessor).drawnChatLines
    val l = chat.lineCount.coerceAtMost(drawnChatLines.size)
    if (j <= MathHelper.floor_float(chat.chatWidth.toFloat() / f) && k < fontRenderer.FONT_HEIGHT * l + l) {
        val m = k / Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + chat.scrollPos
        if (m >= 0 && m < drawnChatLines.size)
            return drawnChatLines[m]
    }
    return null
}

 */