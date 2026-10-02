package org.polyfrost.chatting.compat

import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.lang.reflect.Field

/** references Chat Heads only via reflection strings so it is safe to load when the mod is absent */
object ChatHeadsCompat {

    private val loaded = FabricLoader.getInstance().isModLoaded("chat_heads")

    // never reset per join because JoinGame re-fires on proxy backend switches and the toast would spam
    private var warningShown = false

    private var serverDisabledField: Field? = null

    /** Chat Heads resets this flag on every new connection so re-applying it on JoinGame wins */
    @JvmStatic
    fun reevaluate() {
        val disable = loaded && ChattingConfig.showChatHeads
        try {
            var field = serverDisabledField
            if (field == null) {
                field = Class.forName("dzwdz.chat_heads.ChatHeads").getField("serverDisabledChatHeads")
                serverDisabledField = field
            }
            field.setBoolean(null, disable)
        } catch (e: Throwable) {
            return
        }
        if (disable && !warningShown) {
            Notifications.send(
                "Chatting",
                "The Chat Heads mod is automatically disabled while Chatting's own Chat Heads feature is active to prevent a conflict.",
                NotificationType.ERROR
            )
            warningShown = true
        }
    }
}
