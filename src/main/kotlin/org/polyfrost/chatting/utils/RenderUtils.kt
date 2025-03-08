@file:JvmName("RenderUtils")

package org.polyfrost.chatting.utils

import org.polyfrost.oneconfig.utils.v1.IOUtils
import org.polyfrost.chatting.config.ChattingConfig
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