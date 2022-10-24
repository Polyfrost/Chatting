package cc.woverflow.chatting

import cc.polyfrost.oneconfig.libs.universal.UDesktop
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.dsl.browseLink
import cc.polyfrost.oneconfig.utils.Notifications
import cc.woverflow.chatting.chat.ChatSearchingManager
import cc.woverflow.chatting.chat.ChatShortcuts
import cc.woverflow.chatting.chat.ChatSpamBlock
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.command.ChattingCommand
import cc.woverflow.chatting.config.ChattingConfig
import cc.woverflow.chatting.hook.GuiNewChatHook
import cc.woverflow.chatting.mixin.GuiNewChatAccessor
import cc.woverflow.chatting.utils.ModCompatHooks
import cc.woverflow.chatting.utils.copyToClipboard
import cc.woverflow.chatting.utils.createBindFramebuffer
import cc.woverflow.chatting.utils.screenshot
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.MathHelper
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Mod(
    modid = Chatting.ID,
    name = Chatting.NAME,
    version = Chatting.VER,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object Chatting {

    val keybind = KeyBinding("Screenshot Chat", Keyboard.KEY_NONE, "Chatting")
    const val NAME = "@NAME@"
    const val VER = "@VER@"
    const val ID = "@ID@"
    var doTheThing = false
    var isPatcher = false
        private set
    var isBetterChat = false
        private set
    var isSkytils = false
        private set
    var isHychat = false
        private set

    private val fileFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss'.png'")

    val modDir = File(File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), NAME)

    @Mod.EventHandler
    fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
    }

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        ChattingConfig
        CommandManager.INSTANCE.registerCommand(ChattingCommand())
        ClientRegistry.registerKeyBinding(keybind)
        EVENT_BUS.register(this)
        EVENT_BUS.register(ChatSpamBlock)
        ChatTabs.initialize()
        ChatShortcuts.initialize()
    }

    @Mod.EventHandler
    fun onPostInitialization(event: FMLPostInitializationEvent) {
        isPatcher = Loader.isModLoaded("patcher")
        isBetterChat = Loader.isModLoaded("betterchat")
        isSkytils = Loader.isModLoaded("skytils")
        isHychat = Loader.isModLoaded("hychat")
    }

    @Mod.EventHandler
    fun onForgeLoad(event: FMLLoadCompleteEvent) {
        if (ChattingConfig.informForAlternatives) {
            if (isHychat) {
                Notifications.INSTANCE.send(
                    NAME,
                    "Hychat can be removed as it is replaced by Chatting. Click here for more information.",
                    Runnable {
                        UDesktop.browseLink("https://microcontrollersdev.github.io/Alternatives/1.8.9/hychat")
                    })
            }
            if (isSkytils) {
                try {
                    skytilsCompat(Class.forName("gg.skytils.skytilsmod.core.Config"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        skytilsCompat(Class.forName("skytils.skytilsmod.core.Config"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun skytilsCompat(skytilsClass: Class<*>) {
        val instance = skytilsClass.getDeclaredField("INSTANCE")
        val chatTabs = skytilsClass.getDeclaredField("chatTabs")
        chatTabs.isAccessible = true
        if (chatTabs.getBoolean(instance)) {
            Notifications.INSTANCE.send(
                NAME,
                "Skytils' chat tabs can be disabled as it is replace by Chatting.\nClick here to automatically do this.",
                Runnable {
                    chatTabs.setBoolean(instance, false)
                    ChattingConfig.chatTabs = true
                    ChattingConfig.hypixelOnlyChatTabs = true
                    ChattingConfig.save()
                    skytilsClass.getMethod("markDirty").invoke(instance)
                    skytilsClass.getMethod("writeData").invoke(instance)
                })
        }
        val copyChat = skytilsClass.getDeclaredField("chatTabs")
        copyChat.isAccessible = true
        if (copyChat.getBoolean(instance)) {
            Notifications.INSTANCE.send(
                NAME,
                "Skytils' copy chat messages can be disabled as it is replace by Chatting.\nClick here to automatically do this.",
                Runnable {
                    copyChat.setBoolean(instance, false)
                    skytilsClass.getMethod("markDirty").invoke(instance)
                    skytilsClass.getMethod("writeData").invoke(instance)
                })
        }
    }

    @SubscribeEvent
    fun onTickEvent(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null && (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen is GuiChat)) {
            if (doTheThing) {
                screenshotChat()
                doTheThing = false
            }
        }
    }

    fun getChatHeight(opened: Boolean): Int {
        var height = if (opened) ChattingConfig.focusedHeight else ChattingConfig.unfocusedHeight
        height = (height * Minecraft.getMinecraft().gameSettings.chatScale).toInt()
        val chatY = ModCompatHooks.yOffset + ModCompatHooks.chatPosition
        if (height + chatY + 27 > (UResolution.scaledHeight / Minecraft.getMinecraft().gameSettings.chatScale).toInt() - 27 - chatY) {
            height = (UResolution.scaledHeight / Minecraft.getMinecraft().gameSettings.chatScale).toInt() - 27 - chatY
        }
        return height
    }

    fun screenshotLine(line: ChatLine): BufferedImage? {
        val hud = Minecraft.getMinecraft().ingameGUI
        val chat = hud.chatGUI
        val i = MathHelper.floor_float(chat.chatWidth / chat.chatScale)
        return screenshot(
            GuiUtilRenderComponents.splitText(
                line.chatComponent,
                i,
                Minecraft.getMinecraft().fontRendererObj,
                false,
                false
            ).map { it.formattedText }.reversed()
        )
    }

    private fun screenshotChat() {
        screenshotChat(0)
    }

    fun screenshotChat(scrollPos: Int) {
        val hud = Minecraft.getMinecraft().ingameGUI
        val chat = hud.chatGUI
        val chatLines = ArrayList<String>()
        ChatSearchingManager.filterMessages(
            (chat as GuiNewChatHook).prevText,
            (chat as GuiNewChatAccessor).drawnChatLines
        )?.let { drawnLines ->
            val chatHeight =
                if (ChattingConfig.customChatHeight) getChatHeight(true) / 9 else GuiNewChat.calculateChatboxHeight(
                    Minecraft.getMinecraft().gameSettings.chatHeightFocused / 9
                )
            for (i in scrollPos until drawnLines.size.coerceAtMost(scrollPos + chatHeight)) {
                chatLines.add(drawnLines[i].chatComponent.formattedText)
            }

            screenshot(chatLines)?.copyToClipboard()
        }
    }

    private fun screenshot(messages: List<String>): BufferedImage? {
        if (messages.isEmpty()) {
            Notifications.INSTANCE.send("Chatting", "Chat window is empty.")
            return null
        }
        if (!OpenGlHelper.isFramebufferEnabled()) {
            Notifications.INSTANCE.send(
                "Chatting",
                "Screenshot failed, please disable “Fast Render” in OptiFine’s “Performance” tab."
            )
            return null
        }

        val fr: FontRenderer = ModCompatHooks.fontRenderer
        val width = messages.maxOf { fr.getStringWidth(it) } + 4
        val fb: Framebuffer = createBindFramebuffer(width * 2, (messages.size * 9) * 2)
        val file = File(Minecraft.getMinecraft().mcDataDir, "screenshots/chat/" + fileFormatter.format(Date()))

        GlStateManager.scale(2f, 2f, 1f)
        val scale = Minecraft.getMinecraft().gameSettings.chatScale
        GlStateManager.scale(scale, scale, 1f)
        for (i in messages.indices) {
            ModCompatHooks.redirectDrawString(messages[i], 0f, (messages.size - 1 - i) * 9f, 0xffffff)
        }

        val image = fb.screenshot(file)
        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering()
        Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
        Notifications.INSTANCE.send(
            "Chatting",
            "Chat screenshotted successfully." + (if (ChattingConfig.copyMode != 1) "\nClick to open." else ""),
            Runnable {
                if (!UDesktop.open(file)) {
                    Notifications.INSTANCE.send("Chatting", "Could not browse!")
                }
            })
        return image
    }
}
