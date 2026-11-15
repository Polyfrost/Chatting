@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.chatting.chat

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.texture.DynamicTexture
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.compose.render.PolyBitmap
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.components.PlayerHeads
import org.polyfrost.oneconfig.api.ui.v1.components.PlayerSkinLayers
import org.polyfrost.oneconfig.api.ui.v1.components.PlayerSkinLayersResult
import org.polyfrost.oneconfig.utils.v1.dsl.mc
//? if >=1.21.11 || =1.8.9 {
import net.minecraft.resources.Identifier
//?} else
//import net.minecraft.resources.ResourceLocation as Identifier
//? if = 1.8.9 {
/*import net.minecraft.client.render.texture.TextureManager
*///?}

object HeadTextures {

    class Head(val texture: Identifier, val size: Int, val textureSize: Int) {
        // how far the head overhangs the 8px face
        val inset: Float get() = (size - 8) / 2f
    }

    private class Entry(val color: Int?) {
        var flat: Head? = null
        var threeD: Head? = null
        var unreadable = false // failed to read skin, draw vanilla's head until reset

        val head: Head? get() = if (ChattingConfig.improvedHeads) threeD else flat

        fun textures(): List<Identifier> = listOfNotNull(flat?.texture, threeD?.texture)
    }

    private class Layers(val size: Int, val face: IntArray, val hat: IntArray)

    private const val FALLBACK_COLOR = 0x7F7F7F

    private const val MAX_ENTRIES = 256

    private val evicted = HashMap<Identifier, Entry>()

    @Volatile
    private var reloaded = false

    private val entries = object : LinkedHashMap<Identifier, Entry>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Identifier, Entry>): Boolean {
            if (size <= MAX_ENTRIES) return false
            evicted[eldest.key] = eldest.value
            return true
        }
    }

    fun get(info: PlayerInfo): Head? = entry(skin(info))?.head

    fun texture(info: PlayerInfo, head: Head?): Identifier = head?.texture ?: skin(info)

    fun averageColor(info: PlayerInfo): Int = entry(skin(info))?.color ?: FALLBACK_COLOR

    // may run off the render thread, so tick() does the clearing
    fun invalidate() {
        reloaded = true
    }

    fun tick() {
        if (reloaded) {
            reloaded = false
            evicted.putAll(entries)
            entries.clear()
        }
        if (evicted.isEmpty()) return
        for (entry in evicted.values) entry.textures().forEach(mc.textureManager::release)
        evicted.clear()
    }

    //? if >=1.21.10 {
    private fun skin(info: PlayerInfo): Identifier = info.skin.body().texturePath()
    //?} elif > 1.8.9 {
    /*private fun skin(info: PlayerInfo): Identifier = info.skin.texture()
    *///?} else
    //private fun skin(info: PlayerInfo): Identifier = info.skinTexture

    private fun entry(skin: Identifier): Entry? {
        val cached = entries[skin] ?: evicted.remove(skin)?.also { entries[skin] = it }
        if (cached != null && (cached.unreadable || cached.head != null)) return cached
        val layers = when (val result = PlayerHeads.skinLayers(skin.toString())) {
            is PlayerSkinLayersResult.Loaded -> layers(result.layers)
            PlayerSkinLayersResult.Pending -> return cached
            PlayerSkinLayersResult.Unreadable -> return (cached ?: Entry(null).also { entries[skin] = it }).apply { unreadable = true }
        }
        val entry = cached ?: Entry(average(layers)).also { entries[skin] = it }
        if (ChattingConfig.improvedHeads) entry.threeD = register(skin, "3d", 9 * layers.size, 9) { x, y -> threeDPixel(layers, x, y) }
        else entry.flat = register(skin, "flat", layers.size, 8) { x, y -> flatPixel(layers, x, y) }
        return entry
    }

    private fun layers(skin: PlayerSkinLayers): Layers = Layers(skin.size, skin.face.argb(), skin.hat.argb())

    private fun PolyBitmap.argb(): IntArray = IntArray(width * height) {
        val i = it * 4
        ((pixels[i + 3].toInt() and 0xFF) shl 24) or
            ((pixels[i].toInt() and 0xFF) shl 16) or
            ((pixels[i + 1].toInt() and 0xFF) shl 8) or
            (pixels[i + 2].toInt() and 0xFF)
    }

    private fun flatPixel(layers: Layers, x: Int, y: Int): Int {
        val i = y * layers.size + x
        return over(layers.hat[i], layers.face[i])
    }

    // the hat is drawn 9/8 the size of the face, so each texel covers 9x9 pixels of the hat and 8x8 of the face
    private fun threeDPixel(layers: Layers, x: Int, y: Int): Int {
        val n = layers.size
        val faceX = x - n / 2
        val faceY = y - n / 2
        val face = if (faceX in 0 until 8 * n && faceY in 0 until 8 * n) layers.face[faceY / 8 * n + faceX / 8] else 0
        return over(layers.hat[y / 9 * n + x / 9], face)
    }

    private inline fun register(skin: Identifier, kind: String, textureSize: Int, size: Int, pixel: (Int, Int) -> Int): Head {
        //~ if =1.8.9 'Identifier.fromNamespaceAndPath(' -> 'Identifier('
        val id = Identifier.fromNamespaceAndPath("chatting", "heads/$kind/${skin.namespace}/${skin.path}")
        //? if > 1.8.9 {
        val image = NativeImage(textureSize, textureSize, false)
        for (y in 0 until textureSize) for (x in 0 until textureSize) image.setArgb(x, y, pixel(x, y))
        //? if >=1.21.5 {
        val texture = DynamicTexture(id::toString, image)
        //?} else
        //val texture = DynamicTexture(image)
        // the constructor already uploaded the pixels, so only keep a 1x1 stand-in since close() needs one
        texture.setPixels(NativeImage(1, 1, false))
        //?} else {
        /*val texture = DynamicTexture(textureSize, textureSize)
        for (y in 0 until textureSize) for (x in 0 until textureSize) texture.pixels[y * textureSize + x] = pixel(x, y)
        texture.upload()
        *///?}
        mc.textureManager.register(id, texture)
        return Head(id, size, textureSize)
    }

    private fun over(top: Int, bottom: Int): Int {
        val topAlpha = top ushr 24
        if (topAlpha == 255) return top
        if (topAlpha == 0) return bottom
        val bottomAlpha = (bottom ushr 24) * (255 - topAlpha) / 255
        val alpha = topAlpha + bottomAlpha
        fun channel(shift: Int) = (((top shr shift) and 0xFF) * topAlpha + ((bottom shr shift) and 0xFF) * bottomAlpha) / alpha
        return (alpha shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
    }

    private fun average(layers: Layers): Int? {
        var r = 0
        var g = 0
        var b = 0
        var count = 0
        for (y in 0 until layers.size) for (x in 0 until layers.size) {
            val pixel = flatPixel(layers, x, y)
            if ((pixel ushr 24) < 128) continue
            r += (pixel shr 16) and 0xFF
            g += (pixel shr 8) and 0xFF
            b += pixel and 0xFF
            count++
        }
        if (count == 0) return null
        return ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
    }

    //? if >=1.21.4 {
    private fun NativeImage.setArgb(x: Int, y: Int, argb: Int) = setPixel(x, y, argb)
    //?} else {
    /*private fun NativeImage.setArgb(x: Int, y: Int, argb: Int) = setPixelRGBA(x, y, swapRedBlue(argb))

    private fun swapRedBlue(color: Int): Int =
        (color and 0xFF00FF00.toInt()) or ((color and 0xFF) shl 16) or ((color shr 16) and 0xFF)
    *///?}
}

//? if = 1.8.9 {
/*// 1.8.9's close frees the GL texture but leaves it registered
private fun TextureManager.release(id: Identifier) {
    close(id)
    textures.remove(id)
}
*///?}
