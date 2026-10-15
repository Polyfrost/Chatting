package org.polyfrost.chatting.chat

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
//? if > 1.8.9 {
import net.minecraft.util.FormattedCharSequence
//?} else {
/*import net.minecraft.client.gui.ChatMessage as FormattedCharSequence
*///?}
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.Collections
import java.util.WeakHashMap
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceExtractor
//?} else {
/*import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
*///?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines
//?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.RenderType
*///?} else
//import com.mojang.blaze3d.systems.RenderSystem

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

    private const val SHADOW_OFFSET = 1

    private fun isNormalShadow(): Boolean = ChattingConfig.chatHeadShadow == 1

    fun isLegacyShadow(): Boolean = ChattingConfig.chatHeadShadow == 2

    private fun headY(textY: Int): Int = textY - 1

    private fun headYFraction(): Float = if (ChattingConfig.centerChatHeads) 0.5f else 0f

    private fun darken(color: Int): Int = ((color and 0xFCFCFC) shr 2) or (color and 0xFF000000.toInt())

    private fun shadowColor(alpha: Int): Int = darken((alpha shl 24) or 0xFFFFFF)

    private fun legacyShadowColor(info: PlayerInfo, alpha: Int): Int =
        (alpha shl 24) or (darken(HeadTextures.averageColor(info)) and 0xFFFFFF)

    fun draw(graphics: GuiGraphics, info: PlayerInfo, x: Int, textY: Int, alpha: Int) {
        drawLegacyShadow(graphics, info, x, textY, alpha)
        drawHead(graphics, info, HeadTextures.get(info), x, textY, alpha)
    }

    fun drawLegacyShadow(graphics: GuiGraphics, info: PlayerInfo, x: Int, textY: Int, alpha: Int) {
        if (!isLegacyShadow()) return
        val dy = headYFraction()
        val y = headY(textY) + SHADOW_OFFSET
        translate(graphics, 0f, dy)
        graphics.fill(x + SHADOW_OFFSET, y, x + SHADOW_OFFSET + 8, y + 8, legacyShadowColor(info, alpha))
        translate(graphics, 0f, -dy)
    }

    /** Draws the head and its normal shadow from [HeadTextures.texture] alone, so callers can bind that texture up front. */
    fun drawHead(graphics: GuiGraphics, info: PlayerInfo, head: HeadTextures.Head?, x: Int, textY: Int, alpha: Int) {
        val inset = head?.inset ?: 0f
        val dy = headYFraction() - inset
        val y = headY(textY)
        translate(graphics, -inset, dy)
        if (isNormalShadow()) drawFace(graphics, info, head, x + SHADOW_OFFSET, y + SHADOW_OFFSET, shadowColor(alpha))
        drawFace(graphics, info, head, x, y, (alpha shl 24) or 0xFFFFFF)
        translate(graphics, inset, -dy)
    }

    private fun drawFace(graphics: GuiGraphics, info: PlayerInfo, head: HeadTextures.Head?, x: Int, y: Int, color: Int) {
        if (head == null) {
            //? if >=26.1 {
            PlayerFaceExtractor.extractRenderState(graphics, info.skin, x, y, 8, color)
            //?} elif >=1.21.4 {
            /*PlayerFaceRenderer.draw(graphics, info.skin, x, y, 8, color)
            *///?} else
            //withColor(graphics, color) { PlayerFaceRenderer.draw(graphics, info.skin, x, y, 8) }
            return
        }
        val size = head.textureSize
        //? if >=1.21.4 {
        //~ if <1.21.6 'RenderPipelines.GUI_TEXTURED' -> 'RenderType::guiTextured'
        graphics.blit(RenderPipelines.GUI_TEXTURED, head.texture, x, y, 0f, 0f, head.size, head.size, size, size, size, size, color)
        //?} else
        //withColor(graphics, color) { graphics.blit(head.texture, x, y, head.size, head.size, 0f, 0f, size, size, size, size) }
    }

    private fun translate(graphics: GuiGraphics, x: Float, y: Float) {
        if (x == 0f && y == 0f) return
        //? if >=1.21.6 {
        graphics.pose().translate(x, y)
        //?} else
        //graphics.pose().translate(x, y, 0f)
    }

    //? if <1.21.4 {
    /*private inline fun withColor(graphics: GuiGraphics, color: Int, draw: () -> Unit) {
        RenderSystem.enableBlend()
        graphics.setColor(((color shr 16) and 0xFF) / 255f, ((color shr 8) and 0xFF) / 255f, (color and 0xFF) / 255f, (color ushr 24) / 255f)
        draw()
        graphics.setColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()
    }
    *///?}

    fun shouldOffset(info: PlayerInfo?): Boolean =
        info != null || ChattingConfig.offsetNonPlayerMessages

    fun sameOwner(a: PlayerInfo?, b: PlayerInfo?): Boolean =
        a != null && b != null && a.profile.id == b.profile.id

    fun isHidden(content: FormattedCharSequence): Boolean = hiddenHeads.contains(content)

    //? if >=1.21.10 {
    fun hasServerHeadFor(component: Component, info: PlayerInfo): Boolean {
        val contents = component.contents
        if (contents is net.minecraft.network.chat.contents.ObjectContents) {
            val sprite = contents.contents()
            if (sprite is net.minecraft.network.chat.contents.objects.PlayerSprite &&
                sameOwner(sprite.player().partialProfile(), info)
            ) return true
        }
        return component.siblings.any { hasServerHeadFor(it, info) }
    }

    private fun sameOwner(profile: com.mojang.authlib.GameProfile, info: PlayerInfo): Boolean =
        profile.id == info.profile.id ||
            (profile.name.isNotEmpty() && profile.name.equals(info.profile.name, ignoreCase = true))
    //?} else {
    /*@Suppress("UNUSED_PARAMETER")
    fun hasServerHeadFor(component: Component, info: PlayerInfo): Boolean = false
    *///?}

    fun detect(message: Component): PlayerInfo? {
        val info = detect(message.string) ?: return null
        return if (hasServerHeadFor(message, info)) null else info
    }

    private fun detect(message: String): PlayerInfo? {
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
