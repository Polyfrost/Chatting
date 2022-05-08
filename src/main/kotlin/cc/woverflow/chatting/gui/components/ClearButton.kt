package cc.woverflow.chatting.gui.components

import cc.woverflow.chatting.Chatting
import gg.essential.api.EssentialAPI
import gg.essential.api.gui.buildConfirmationModal
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.ChatColor
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

class ClearButton :
    CleanButton(13379014, { UResolution.scaledWidth - 28 }, { UResolution.scaledHeight - 27 }, 12, 12, "",
        { RenderType.NONE }) {

    override fun onMousePress() {
        EssentialAPI.getGuiUtil().openScreen(object : WindowScreen(ElementaVersion.V1, restoreCurrentGuiOnClose = true, drawDefaultBackground = false) {
            init {
                UIBlock(Color.BLACK.withAlpha(0.3f)) childOf window
                EssentialAPI.getEssentialComponentFactory().buildConfirmationModal {
                    text = "${ChatColor.RED}Are ${ChatColor.RED}you ${ChatColor.RED}sure ${ChatColor.RED}you ${ChatColor.RED}want ${ChatColor.RED}to ${ChatColor.RED}clear ${ChatColor.RED}the ${ChatColor.RED}chat?${ChatColor.RESET}"
                    secondaryText = "${ChatColor.BOLD}This${ChatColor.BOLD} ${ChatColor.BOLD}is${ChatColor.BOLD} ${ChatColor.BOLD}irreversible.${ChatColor.RESET}"

                    onConfirm = {
                        Minecraft.getMinecraft().ingameGUI.chatGUI.clearChatMessages()
                        restorePreviousScreen()
                    }
                    onDeny = { restorePreviousScreen() }
                } childOf window
            }
        })
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
        super.drawButton(mc, mouseX, mouseY)
        if (visible) {
            if (hovered) GlStateManager.color(1f, 1f, 160f / 255f)
            else GlStateManager.color(1f, 1f, 1f)
            mc.textureManager.bindTexture(ResourceLocation(Chatting.ID, "delete.png"))
            Gui.drawModalRectWithCustomSizedTexture(xPosition + 1, yPosition + 1, 0f, 0f, 10, 10, 10f, 10f)
        }
    }
}