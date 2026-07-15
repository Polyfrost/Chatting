package org.polyfrost.chatting.compat

import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.lang.reflect.Field

/**
 * Suppresses the third-party Chat Heads mod's head rendering and chat offset while Chatting's own Chat
 * Heads feature is active, without ever touching Chat Heads' saved config.
 *
 * References Chat Heads only via reflection strings, so it is safe to load on every version and when the
 * mod is absent.
 */
object ChatHeadsCompat {

    private val loaded = FabricLoader.getInstance().isModLoaded("chat_heads")

    // Toast guard: shown at most once per game session. Reevaluation fires on every JoinGame packet
    // (including proxy/BungeeCord backend switches on servers like Hypixel, which re-send JoinGame over
    // the same connection), so this must NOT reset per join or the toast would spam on every world change.
    private var warningShown = false

    private var serverDisabledField: Field? = null

    /**
     * Resolves `ChatHeads.serverDisabledChatHeads` lazily. The Chat Heads mod flips this flag itself (via a
     * server "disable" resource pack) and resets it to `false` on every new connection, so re-applying our
     * value on JoinGame cleanly wins. Failures are never latched: this only runs from [reevaluate] (a rare
     * transition, never a hot path), so a transient failure self-heals on the next call.
     */
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
