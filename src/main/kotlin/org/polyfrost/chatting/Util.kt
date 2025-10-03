@file:JvmName("Util")

package org.polyfrost.chatting

import com.mojang.authlib.GameProfile
import dev.deftu.clipboard.Clipboard
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.resourceManager
import dev.deftu.omnicore.api.color.OmniColor
import net.minecraft.client.gui.hud.ChatHudLine
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.ChatLineElement
import org.polyfrost.chatting.mixin.ChatAccessor
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.oneconfig.api.ui.v1.UIManager
import org.polyfrost.oneconfig.internal.DynamicPolyImage
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.unit.seconds
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.max

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val editorMessages = mutableListOf(
    "§b§lChatting",
    "",
    "This is a movable chat",
    "§eDrag me around!"
)

val WHITE = OmniColor(-1)

@JvmField
var currentSender: GameProfile? = null

fun getMessages(): List<
        //#if MC == 11605
        //$$ ChatHudLine<net.minecraft.text.Text>,
        //#else
        ChatHudLine
        //#endif
        > {
    return (mc.inGameHud.chatHud as ChatAccessor).messages
}

//fun OrderedText.asString(): String {
//    val sb = StringBuilder()
//    var lastStyle = Style.EMPTY
//
//    this.accept { _, style, codePoint ->
//        if (style != lastStyle) {
//            sb.append(getFormattingCodes(style, lastStyle))
//            lastStyle = style
//        }
//        sb.appendCodePoint(codePoint)
//        true
//    }
//
//    return sb.toString()
//}
//
//private fun getFormattingCodes(style: Style, old: Style): String {
//    val stringBuilder = StringBuilder()
//
//    if (!style.isBold && old.isBold ||
//        !style.isItalic && old.isItalic ||
//        !style.isUnderlined && old.isUnderlined ||
//        !style.isStrikethrough && old.isStrikethrough ||
//        !style.isObfuscated && old.isObfuscated ||
//        (style.color != old.color && style.color == null)) {
//        stringBuilder.append("§r")
//    }
//
//    val color = style.color
//    if (color != null) {
//        val format = Formatting.byName(color.name)
//        if (format != null) {
//            stringBuilder.append('§').append(format.code)
//        } else {
//            val hex = String.format("%06x", color.rgb)
//            stringBuilder.append("§x")
//            for (c in hex) stringBuilder.append('§').append(c)
//        }
//    }
//
//    if (style.isBold) stringBuilder.append("§l")
//    if (style.isItalic) stringBuilder.append("§o")
//    if (style.isUnderlined) stringBuilder.append("§n")
//    if (style.isStrikethrough) stringBuilder.append("§m")
//    if (style.isObfuscated) stringBuilder.append("§k")
//
//    return stringBuilder.toString()
//}

//fun getHoveredComponent(): ChatComponent? {
//    val mouseOver = UIManager.INSTANCE.defaultInstance.inputManager.mouseOver ?: return null
//    if (mouseOver !is ChatComponent) return null
//    return mouseOver
//}
//
//fun getLineMouseX(chatComponent: ChatComponent, chatLineElement: ChatLineElement): Int {
//    return ((OmniMouse.rawX - chatComponent.x) * OmniResolution.scaledWidth / max(1, OmniResolution.windowWidth)).toInt() - 4 - if (chatLineElement.hasHead) 10 else 0
//}

//fun getIndicator(): MessageIndicator? {
//    val chatComponent = getHoveredComponent() ?: return null
//    if (chatComponent.currentHovered == -1) return null
//    val element = chatComponent.elements[chatComponent.currentHovered]
//    val indicator = element.visible.indicator ?: return null
//    if (getLineMouseX(chatComponent, element) >= 0) return null
//    return indicator
//}
//
//fun getStyle(): Style? {
//    val chatComponent = getHoveredComponent() ?: return null
//    if (chatComponent.currentHovered != -1) {
//        val element = chatComponent.elements[chatComponent.currentHovered]
//        val mouseX = getLineMouseX(chatComponent, element)
//        return mc.textRenderer.textHandler.getStyleAt(element.visible.comp_896, mouseX)
//    }
//    return null
//}

// messy code
@Throws(IOException::class)
fun InputStream.cropToInputStream(x: Int, y: Int, width: Int, height: Int): InputStream {
    ByteArrayOutputStream().use { stream ->
        ImageIO.write(ImageIO.read(this).getSubimage(x, y, width, height), "png", stream)
        stream.flush()
        return ByteArrayInputStream(stream.toByteArray())
    }
}

fun getSkinFromProfile(gameProfile: GameProfile?): PolyImage? {
    val profile = gameProfile ?: return null

    mc.networkHandler?.getPlayerListEntry(profile.id)?.let {
        val stream =
            //#if MC > 1.16.5
            resourceManager.open(it.skinTextures.comp_1626)
            //#else
            //$$ resourceManager.getResource(it.skinTexture).inputStream
            //#endif
        stream?.cropToInputStream(8, 8, 8, 8)?.let { stream ->
            return DynamicPolyImage("chatHead_${gameProfile.name}", stream, PolyImage.Type.Raster)
        }
    }
    return null
}

fun String.copyToClipboard() {
    Clipboard.getInstance().string = this
    Notifications.enqueue(Notifications.Type.Info, "Chatting", "Copied \"$this\" to clipboard.", 3.seconds)
}