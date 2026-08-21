package org.polyfrost.chatting.chat

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.FormattedCharSequence
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.Collections
import java.util.WeakHashMap

//? if >=1.21.11 {
private typealias SkinTexture = net.minecraft.resources.Identifier
//?} else {
/*private typealias SkinTexture = net.minecraft.resources.ResourceLocation
*///?}

object ChatHeads {

    private val SPLIT = Regex("\\W")

    private val headLines: MutableMap<FormattedCharSequence, PlayerInfo> =
        Collections.synchronizedMap(WeakHashMap())

    private val hiddenHeads: MutableSet<FormattedCharSequence> =
        Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap()))

    fun tag(content: FormattedCharSequence, info: PlayerInfo?, hidden: Boolean) {
        if (info == null) {
            headLines.remove(content)
            hiddenHeads.remove(content)
        } else {
            headLines[content] = info
            if (hidden) hiddenHeads.add(content) else hiddenHeads.remove(content)
        }
    }

    fun lookup(content: FormattedCharSequence): PlayerInfo? = headLines[content]

    fun shouldDrawHead(info: PlayerInfo?, hidden: Boolean): Boolean = info != null && !hidden

    const val SHADOW_OFFSET = 1

    fun shouldDrawShadow(): Boolean = ChattingConfig.chatHeadShadow != 0

    fun isLegacyShadow(): Boolean = ChattingConfig.chatHeadShadow == 2

    fun headY(textY: Int): Int = textY - 1

    fun headYFraction(): Float = if (ChattingConfig.centerChatHeads) 0.5f else 0f

    fun darken(color: Int): Int = ((color and 0xFCFCFC) shr 2) or (color and 0xFF000000.toInt())

    fun shadowColor(alpha: Int): Int = darken((alpha shl 24) or 0xFFFFFF)

    fun shadowColor(info: PlayerInfo, alpha: Int): Int =
        if (isLegacyShadow()) (alpha shl 24) or (darken(faceColor(skinTexture(info))) and 0xFFFFFF)
        else shadowColor(alpha)

    private const val FALLBACK_FACE_COLOR = 0x7F7F7F

    private val faceColors = HashMap<SkinTexture, Int>()

    private fun skinTexture(info: PlayerInfo): SkinTexture =
        info.skin/*? if >=1.21.10 {*/.body().texturePath()/*?} else {*//*.texture()*//*?}*/

    private fun faceColor(texture: SkinTexture): Int {
        faceColors[texture]?.let { return it }
        val color = computeFaceColor(texture) ?: return FALLBACK_FACE_COLOR
        faceColors[texture] = color
        return color
    }

    private fun computeFaceColor(texture: SkinTexture): Int? {
        val loaded = runCatching { mc.textureManager.getTexture(texture) }.getOrNull()
        if (loaded is DynamicTexture) {
            runCatching { loaded.pixels?.let { return average(it) } }
        }
        return runCatching {
            mc.resourceManager.open(texture).use { stream ->
                NativeImage.read(stream).use { average(it) }
            }
        }.getOrNull()
    }

    private fun average(image: NativeImage): Int? {
        if (image.width < 48 || image.height < 16) return null
        var r = 0
        var g = 0
        var b = 0
        var count = 0
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                var pixel = pixel(image, 8 + x, 8 + y)
                val hat = pixel(image, 40 + x, 8 + y)
                if ((hat ushr 24) >= 128) pixel = hat
                if ((pixel ushr 24) < 128) continue
                r += (pixel shr 16) and 0xFF
                g += (pixel shr 8) and 0xFF
                b += pixel and 0xFF
                count++
            }
        }
        if (count == 0) return null
        return ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
    }

    private fun pixel(image: NativeImage, x: Int, y: Int): Int {
        //? if >=1.21.4 {
        return image.getPixel(x, y)
        //?} else {
        /*val abgr = image.getPixelRGBA(x, y)
        return (abgr and 0xFF00FF00.toInt()) or ((abgr and 0xFF) shl 16) or ((abgr shr 16) and 0xFF)
        *///?}
    }

    fun shouldOffset(info: PlayerInfo?): Boolean =
        info != null || ChattingConfig.offsetNonPlayerMessages

    fun sameOwner(a: PlayerInfo?, b: PlayerInfo?): Boolean =
        a != null && b != null && a.profile.id == b.profile.id

    fun isHidden(content: FormattedCharSequence): Boolean = hiddenHeads.contains(content)

    fun detect(message: String): PlayerInfo? {
        val connection = mc.connection ?: return null
        val before = message.substringBefore(":")
        val words = SPLIT.split(before).filter { it.isNotEmpty() }
        if (words.isEmpty()) return null

        for (word in words) {
            connection.getPlayerInfo(word)?.let { return it }
        }

        for (player in connection.onlinePlayers) {
            val displayName = player.tabListDisplayName?.string ?: continue
            if (words.any { it == displayName }) return player
        }
        return null
    }
}
