package org.polyfrost.chatting.compat

import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Temporarily suppresses the third-party Text Tunnels mod's chat-line hiding and tunnel buttons while
 * Chatting's Chat Tabs feature is active, without ever touching Text Tunnels' saved config.
 *
 * References Text Tunnels only via reflection strings, so it is safe to load on every version and when
 * the mod is absent.
 */
object TextTunnelsCompat {

    private val loaded = FabricLoader.getInstance().isModLoaded("text_tunnels")

    @Volatile
    @JvmStatic
    var suppressing = false
        private set

    // Toast guard: shown at most once per game session. Reevaluation fires on every JoinGame packet
    // (including proxy/BungeeCord backend switches on servers like Hypixel, which re-send JoinGame over
    // the same connection), so this must NOT reset per join or the toast would spam on every world change.
    private var warningShown = false

    private var getMethod: Method? = null
    private var mainConfigField: Field? = null
    private var enabledField: Field? = null

    private fun shouldSuppressTextTunnels(): Boolean =
        loaded && ChattingConfig.chatTabs && isTextTunnelsEnabled()

    /**
     * Reflectively reads `ConfigManager.get().mainConfig.enabled`. Resolved handles are cached lazily, but
     * a failure is never latched: this only runs from [reevaluate] (a rare transition, never a hot path), so
     * a transient failure (e.g. Text Tunnels' config not yet initialized) self-heals on the next call.
     */
    private fun isTextTunnelsEnabled(): Boolean {
        return try {
            var method = getMethod
            if (method == null) {
                method = Class.forName("org.olim.text_tunnels.config.ConfigManager").getMethod("get")
                getMethod = method
            }
            val manager = method.invoke(null)
            var mainField = mainConfigField
            if (mainField == null) {
                mainField = manager.javaClass.getField("mainConfig")
                mainConfigField = mainField
            }
            val mainConfig = mainField.get(manager)
            var enabled = enabledField
            if (enabled == null) {
                enabled = mainConfig.javaClass.getField("enabled")
                enabledField = enabled
            }
            enabled.getBoolean(mainConfig)
        } catch (e: Throwable) {
            false
        }
    }

    @JvmStatic
    fun reevaluate() {
        val now = shouldSuppressTextTunnels()
        if (now && !warningShown) {
            Notifications.send(
                "Chatting",
                "Text Tunnels is automatically disabled while Chat Tabs is active to prevent a conflict.",
                NotificationType.ERROR
            )
            warningShown = true
        }
        suppressing = now
    }
}
