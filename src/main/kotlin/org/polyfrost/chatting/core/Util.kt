@file:JvmName("Util")

package org.polyfrost.chatting.core

import com.mojang.authlib.GameProfile
import dev.deftu.clipboard.Clipboard
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.render.OmniTextRenderer
import dev.deftu.omnicore.api.client.render.pipeline.OmniRenderPipelines
import dev.deftu.omnicore.api.client.render.stack.OmniMatrixStack
import dev.deftu.omnicore.api.client.resourceManager
import dev.deftu.omnicore.api.color.ColorFormat
import dev.deftu.omnicore.api.color.OmniColor
import dev.deftu.textile.TextStyle
import dev.deftu.textile.minecraft.asVanilla
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.polyfrost.chatting.component.ChatComponent
import org.polyfrost.chatting.component.PlayerHead
import org.polyfrost.chatting.mixin.ChatAccessor
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.oneconfig.internal.DynamicPolyImage
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.unit.seconds
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import javax.imageio.ImageIO

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val editorMessages = mutableListOf(
    "§b§lChatting",
    "",
    "This is a movable chat",
    "§eDrag me around!"
)

val WHITE = OmniColor(-1)

var hoveredComponent: ChatComponent? = null

val chatComponents = ArrayList<ChatComponent>()

var playerHeads = HashMap<UUID, PlayerHead>()

@JvmField
var currentSender: GameProfile? = null

fun getMessages(): List<McChatLine> {
    return (mc.inGameHud.chatHud as ChatAccessor).messages
}

fun String.toChatLine(): McChatLine {
    return ChatHudLine(
        -1,
        //#if MC >= 1.16.5
        Text.literal(this),
        //#else
        //$$ net.minecraft.util.text.TextComponentString(this),
        //#endif
        //#if MC > 1.16.5
        null,
        null
        //#else
        //$$ -1
        //#endif
    )
}

typealias McChatLine =
    ChatHudLine
    //#if MC == 1.16.5
    //$$ <net.minecraft.text.Text>
    //#endif

//#if MC > 1.16.5
fun getIndicatorAt(): net.minecraft.client.gui.hud.MessageIndicator? {
    val component = hoveredComponent ?: return null
    val element = component.currentHovered ?: return null
    val scale = mcScale * component.scaleX.coerceAtLeast(0.001f)
    val xPos = ((OmniMouse.rawX - component.x) / scale) - 4 - element.lineOffset
    val messageIndicator = element.fullMessage.indicator ?: return null
    if (xPos < 0) return messageIndicator
    val icon = messageIndicator.comp_900 ?: return null
    val indicatorX = OmniTextRenderer.width(element.text) + 4 + element.lineOffset
    if (xPos - indicatorX in 0f..icon.width.toFloat()) return messageIndicator
    return null
}
//#endif

fun hasClickEventAt(): Boolean {
    val style = getStyleAt() ?: return false
    return style.clickEvent != null
}

fun getStyleAt(): Style? {
    return getTextAt()?.style
}

fun getTextAt(): Text? {
    val component = hoveredComponent ?: return null
    val element = component.currentHovered ?: return null
    val scale = mcScale * component.scaleX.coerceAtLeast(0.001f)
    val xPos = ((OmniMouse.rawX - component.x) / scale) - 4 - element.lineOffset
    if (xPos < 0) return null
    var advance = 0
    return visitNode(element.text) { node, segment, style ->
        val width = OmniTextRenderer.width(dev.deftu.textile.Text.literal(segment).setStyle(style))
        advance += width
        if (advance > xPos) {
            node.asVanilla()
        } else {
            null
        }
    }
}

fun <T> visitNode(
    text: dev.deftu.textile.Text,
    inheritedStyle: TextStyle = TextStyle.EMPTY,
    visitor: (node: dev.deftu.textile.Text, content: String, style: TextStyle) -> T?
): T? {
    val style = text.style.inherited(inheritedStyle)
    val hit = text.content.visit({ content, innerStyle ->
        if (content.isEmpty()) {
            return@visit null
        }

        visitor(text, content, style)
    }, style)

    if (hit != null) {
        return hit
    }

    for (sibling in text.siblings) {
        val siblingHit = visitNode(sibling, style, visitor)
        if (siblingHit != null) {
            return siblingHit
        }
    }

    return null
}

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

fun withAlpha(i: Int, j: Int): OmniColor {
    return OmniColor(ColorFormat.ARGB, i shl 24 or (j and 16777215))
}

fun OmniMatrixStack.renderQuad(
    x: Double, y: Double,
    width: Double, height: Double,
    color: OmniColor,
) {
    val pipeline = OmniRenderPipelines.POSITION_COLOR
    val buffer = pipeline.createBufferBuilder()
    buffer.quad(this, x, y, width, height, color)
    buffer.buildOrThrow().drawAndClose(pipeline)
}

fun String.copyToClipboard() {
    Clipboard.getInstance().string = this
    Notifications.enqueue(Notifications.Type.Info, "Chatting", "Copied \"$this\" to clipboard.", 3.seconds)
}