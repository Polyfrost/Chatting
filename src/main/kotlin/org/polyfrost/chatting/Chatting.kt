package org.polyfrost.chatting

import cc.polyfrost.oneconfig.libs.universal.UDesktop
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.dsl.browseLink
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.polyfrost.chatting.chat.ChatScrollingHook.shouldSmooth
import org.polyfrost.chatting.chat.ChatSearchingManager
import org.polyfrost.chatting.chat.ChatShortcuts
import org.polyfrost.chatting.chat.ChatSpamBlock
import org.polyfrost.chatting.chat.ChatTabs
import org.polyfrost.chatting.command.ChattingCommand
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.mixin.GuiNewChatAccessor
import org.polyfrost.chatting.utils.ModCompatHooks
import org.polyfrost.chatting.utils.copyToClipboard
import org.polyfrost.chatting.utils.createBindFramebuffer
import org.polyfrost.chatting.utils.screenshot
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

    private var lastPressed = false;
    var peeking = false
        get() = ChattingConfig.chatPeek && field

    private val fileFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss'.png'")

    val oldModDir = File(File(mc.mcDataDir, "W-OVERFLOW"), NAME)

    @Mod.EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        ChattingConfig
        if (!ChattingConfig.enabled) {
            ChattingConfig.enabled = true
            ChattingConfig.save()
        }
        if (!ChattingConfig.chatWindow.transferOverScale) {
            ChattingConfig.chatWindow.normalScale = ChattingConfig.chatWindow.scale
            ChattingConfig.chatWindow.transferOverScale = true
            ChattingConfig.save()
        }
        ChattingConfig.chatWindow.updateMCChatScale()
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
            if (isBetterChat) {
                Notifications.INSTANCE.send(
                    NAME,
                    "BetterChat can be removed as it is replaced by Chatting. Click here to open your mods folder to delete the BetterChat file.",
                    Runnable {
                        UDesktop.open(File("./mods"))
                    })
            }
        }
    }

    private fun skytilsCompat(skytilsClass: Class<*>) {
        val instance = skytilsClass.getDeclaredField("INSTANCE").get(null)
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
        val copyChat = skytilsClass.getDeclaredField("copyChat")
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
        if (event.phase == TickEvent.Phase.START && mc.theWorld != null && mc.thePlayer != null) {
            if ((mc.currentScreen == null || mc.currentScreen is GuiChat)) {
                if (doTheThing) {
                    screenshotChat()
                    doTheThing = false
                }
            }

            var canScroll = true

            val key = ChattingConfig.chatPeekBind
            if (key.isActive != lastPressed && ChattingConfig.chatPeek) {
                lastPressed = key.isActive
                if (key.isActive) {
                    peeking = !peeking
                } else if (!ChattingConfig.peekMode) {
                    peeking = false
                }
                canScroll = false
                if (!peeking) mc.ingameGUI.chatGUI.resetScroll()
            }

            if (mc.currentScreen is GuiChat) peeking = false

            if (peeking && ChattingConfig.peekScrolling) {
                var i = Mouse.getDWheel().coerceIn(-1..1)

                if (i != 0) {

                    if (!GuiScreen.isShiftKeyDown()) {
                        i *= 7
                    }

                    shouldSmooth = true
                    if (canScroll) mc.ingameGUI.chatGUI.scroll(i)
                }
            }
        }
    }

    fun getChatHeight(opened: Boolean): Int {
        return if (opened) ChattingConfig.chatWindow.focusedHeight else ChattingConfig.chatWindow.unfocusedHeight
    }

    fun getChatWidth(): Int {
        return ChattingConfig.chatWindow.customWidth
    }

    fun screenshotLine(line: ChatLine?): BufferedImage? {
        if (line == null || line !is ChatLineHook) {
            Notifications.INSTANCE.send("Chatting", "No chat line provided.")
            return null
        }

        return screenshot(
            linkedMapOf<ChatLine, String>().also { map ->
                val fullMessage = (line as ChatLineHook).`chatting$getFullMessage`()
                if (fullMessage == null) {
                    Notifications.INSTANCE.send("Chatting", "No full message found for the provided chat line.")
                    return null
                }

                for (chatLine in (mc.ingameGUI.chatGUI as GuiNewChatAccessor).drawnChatLines) {
                    if (chatLine == null || chatLine !is ChatLineHook) {
                        continue
                    }

                    if ((chatLine as ChatLineHook).`chatting$getFullMessage`() == fullMessage) {
                        map[chatLine] = chatLine.chatComponent.formattedText
                    }
                }
            }
        )
    }

    private fun screenshotChat() {
        screenshotChat(0)
    }

    fun screenshotChat(scrollPos: Int) {
        val chatLines = LinkedHashMap<ChatLine, String>()
        ChatSearchingManager.filteredMessages.let { drawnLines ->
            val chatHeight =
                if (ChattingConfig.chatWindow.customChatHeight) getChatHeight(true) / 9 else GuiNewChat.calculateChatboxHeight(
                    mc.gameSettings.chatHeightFocused / 9
                )
            for (i in scrollPos until drawnLines.size.coerceAtMost(scrollPos + chatHeight)) {
                chatLines[drawnLines[i]] = drawnLines[i].chatComponent.formattedText
            }

            screenshot(chatLines)?.copyToClipboard()
        }
    }

    private fun screenshot(messages: HashMap<ChatLine, String>): BufferedImage? {
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

        val fr = ModCompatHooks.fontRenderer
        val border = ChattingConfig.textRenderType == 2
        val offset = if (border) 1 else 0
        val width = messages.maxOf { fr.getStringWidth(it.value) + (if (ChattingConfig.showChatHeads && ((it.key as ChatLineHook).`chatting$hasDetected`() || ChattingConfig.offsetNonPlayerMessages)) 10 else 0) } + if (border) 2 else 1
        val fb = createBindFramebuffer(width * 2, (messages.size * 9 + offset) * 2)
        val file = File(mc.mcDataDir, "screenshots/chat/" + fileFormatter.format(Date()))

        GlStateManager.scale(2f, 2f, 1f)
        messages.entries.forEachIndexed { i: Int, entry: MutableMap.MutableEntry<ChatLine, String> ->
            ModCompatHooks.redirectDrawString(entry.value, offset.toFloat(), (messages.size - 1 - i) * 9f + offset.toFloat(), 0xFFFFFFFF.toInt(), entry.key)
        }

        val image = fb.screenshot(file)
        mc.entityRenderer.setupOverlayRendering()
        mc.framebuffer.bindFramebuffer(true)
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
