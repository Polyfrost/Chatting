package org.polyfrost.chatting.compat

import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Suppresses the third-party Chat Impressive Animation mod's message sending animation while Chatting's own
 * Smooth Chat Messages feature is active, restoring the user's original preference when ours is turned off.
 *
 * References Chat Impressive Animation only via reflection strings, so it is safe to load on every version and
 * when the mod is absent (e.g. on 26.2, which the mod does not yet ship a build for). Any internal change to the
 * mod's config API is caught and swallowed rather than crashing.
 */
object ChatImpressiveAnimationCompat {

    private val loaded = FabricLoader.getInstance().isModLoaded("chatimpressiveanimation")

    // Toast guard: shown at most once per game session. Reevaluation fires on every JoinGame packet
    // (including proxy/BungeeCord backend switches on servers like Hypixel, which re-send JoinGame over
    // the same connection), so this must NOT reset per join or the toast would spam on every world change.
    private var warningShown = false

    private var getConfigMethod: Method? = null
    private var enableField: Field? = null

    // The user's original value, captured on the transition into suppression. Unlike Chat Heads' mod-owned
    // runtime flag, this field is a user preference, so it must be saved and restored rather than clobbered.
    private var savedValue: Boolean? = null

    /**
     * Reflectively flips `ConfigUtil.getConfig().enableChatSendingAnimation` to match our suppression state.
     * This mutates only the live in-memory config; Cloth AutoConfig does not persist unless its own screen
     * saves. Resolution is cached lazily and failures are never latched: this only runs from a rare transition
     * (JoinGame or our own config callback), so a transient failure self-heals on the next call.
     */
    @JvmStatic
    fun reevaluate() {
        if (!loaded) return
        val suppress = ChattingConfig.smoothChat

        val config: Any
        val field: Field
        try {
            var method = getConfigMethod
            if (method == null) {
                method = Class.forName("com.wulian.chatimpressiveanimation.config.ConfigUtil")
                    .getMethod("getConfig")
                getConfigMethod = method
            }
            config = method.invoke(null)
            field = enableField ?: Class.forName("com.wulian.chatimpressiveanimation.config.ModConfigs")
                .getField("enableChatSendingAnimation").also { enableField = it }

            if (suppress) {
                if (savedValue == null) savedValue = field.getBoolean(config)
                field.setBoolean(config, false)
            } else if (savedValue != null) {
                field.setBoolean(config, savedValue!!)
                savedValue = null
            }
        } catch (e: Throwable) {
            return
        }

        if (suppress && !warningShown) {
            Notifications.send(
                "Chatting",
                "Chat Impressive Animation is partially disabled while Smooth Chat Messages is active to prevent a conflict.",
                NotificationType.ERROR
            )
            warningShown = true
        }
    }
}
