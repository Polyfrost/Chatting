package org.polyfrost.chatting.component

import org.polyfrost.chatting.core.mcScale
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.impl.Block
import org.polyfrost.polyui.component.impl.Image
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.by

class ChatButton(image: PolyImage, at: Vec2): Block(Image(image, at = at.x + mcScale by at.y + mcScale), at = at, size = 9 * mcScale by 9 * mcScale, color = rgba(0, 0, 0, 0.25f)) {

}