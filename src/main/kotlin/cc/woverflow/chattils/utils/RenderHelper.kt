package cc.woverflow.chattils.utils

import cc.woverflow.chattils.config.ChattilsConfig
import cc.woverflow.chattils.hook.GuiNewChatHook
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
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


object RenderHelper {
    private val regex = Regex("(?i)\\u00A7[0-9a-f]")
    var bypassWyvtils = false
    private set

    /**
     * Taken from https://github.com/Moulberry/HyChat
     * Modified so if not on Windows just in case it will switch it to RGB and remove the transparent background.
     */
    fun copyBufferedImageToClipboard(bufferedImage: BufferedImage) {
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                val width = bufferedImage.width
                val height = bufferedImage.height
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
                        val argb: Int = bufferedImage.getRGB(x, height - y - 1)
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
                        val argb: Int = bufferedImage.getRGB(x, height - y - 1)
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
            bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, null, 0, bufferedImage.width)
        val newImage = BufferedImage(bufferedImage.width, bufferedImage.height, BufferedImage.TYPE_INT_RGB)
        newImage.setRGB(0, 0, newImage.width, newImage.height, pixels, 0, newImage.width)

        try {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(ImageTransferable(bufferedImage), null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Taken from https://github.com/Moulberry/HyChat
     */
    fun screenshotFramebuffer(framebuffer: Framebuffer, file: File): BufferedImage {
        val w = framebuffer.framebufferWidth
        val h = framebuffer.framebufferHeight
        val i = w * h
        val pixelBuffer = BufferUtils.createIntBuffer(i)
        val pixelValues = IntArray(i)
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        GlStateManager.bindTexture(framebuffer.framebufferTexture)
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer)
        pixelBuffer[pixelValues] //Load buffer into array
        TextureUtil.processPixelValues(pixelValues, w, h) //Flip vertically
        val bufferedimage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val j = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight
        for (k in j until framebuffer.framebufferTextureHeight) {
            for (l in 0 until framebuffer.framebufferWidth) {
                bufferedimage.setRGB(l, k - j, pixelValues[k * framebuffer.framebufferTextureWidth + l])
            }
        }
        if (ChattilsConfig.copyMode != 1) {
            try {
                file.parentFile.mkdirs()
                ImageIO.write(bufferedimage, "png", file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bufferedimage
    }

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
     */
    fun drawBorderedString(
        fontRendererIn: FontRenderer,
        text: String,
        x: Int,
        y: Int,
        color: Int
    ): Int {
        val noColors = text.replace(regex, "\u00A7r")
        var yes = 0
        if (((Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).textOpacity / 4) > 3) {
            bypassWyvtils = true
            for (xOff in -2..2) {
                for (yOff in -2..2) {
                    if (xOff * xOff != yOff * yOff) {
                        yes += fontRendererIn.drawString(
                            noColors,
                            (xOff / 2f) + x, (yOff / 2f) + y, ((Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatHook).textOpacity / 4) shl 24, false
                        )
                    }
                }
            }
            bypassWyvtils = false
        }
        yes += fontRendererIn.drawString(text, x, y, color)
        return yes
    }
}