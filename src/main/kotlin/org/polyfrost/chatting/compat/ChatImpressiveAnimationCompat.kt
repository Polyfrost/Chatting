package org.polyfrost.chatting.compat

import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.lang.reflect.Field
import java.lang.reflect.Method

/** references Chat Impressive Animation only via reflection strings so it is safe to load when the mod is absent */
object ChatImpressiveAnimationCompat {

    private val loaded = FabricLoader.getInstance().isModLoaded("chatimpressiveanimation")

    // never reset per join because JoinGame re-fires on proxy backend switches and the toast would spam
    private var warningShown = false

    private var getConfigMethod: Method? = null
    private var enableField: Field? = null

    // a user preference rather than a mod owned runtime flag so it must be saved and restored not clobbered
    private var savedValue: Boolean? = null

    /** mutates only the live in memory config since Cloth AutoConfig persists only when its own screen saves */
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
