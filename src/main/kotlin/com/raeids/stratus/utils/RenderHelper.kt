package com.raeids.stratus.utils

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object RenderHelper {
    /**
     * Taken from https://github.com/Moulberry/HyChat
     */
    fun screenshotFramebuffer(framebuffer: Framebuffer, file: File) {
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
        try {
            file.parentFile.mkdirs()
            ImageIO.write(bufferedimage, "png", file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
}