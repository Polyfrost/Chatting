package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatLineHeadHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Applies Chatting's text-shadow setting through the vanilla chat renderer. */
@Mixin(value = GuiNewChat.class, priority = 990)
public class GuiNewChatMixin_TextRendering {
    @Shadow @Final private Minecraft mc;
    @Unique private ChatLine chatting$currentLine;
    @Unique private final ModelPlayer chatting$headModel = new ModelPlayer(0.0F, false);

    @ModifyVariable(method = "drawChat", at = @At("STORE"), ordinal = 0)
    private ChatLine chatting$captureChatLine(ChatLine line) {
        chatting$currentLine = line;
        return line;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int chatting$drawChatText(FontRenderer renderer, String text, float x, float y, int color) {
        float textX = x;
        if (ChattingConfig.INSTANCE.getShowChatHeads() && chatting$currentLine instanceof ChatLineHeadHook) {
            ChatLineHeadHook line = (ChatLineHeadHook) chatting$currentLine;
            if (line.chatting$hasDetectedPlayer() || ChattingConfig.INSTANCE.getOffsetNonPlayerMessages()) textX += 10.0F;
            NetworkPlayerInfo player = line.chatting$getPlayerInfo();
            if (ChattingConfig.INSTANCE.getChatHeadShadow() != 0) chatting$drawHead(player, x + 1.0F, y + 1.0F, color, true);
            chatting$drawHead(player, x, y, color, false);
        }
        return renderer.drawString(text, textX, y, color, ChattingConfig.INSTANCE.getTextRenderType() != 0);
    }

    @Unique
    private void chatting$drawHead(NetworkPlayerInfo player, float x, float y, int color, boolean shadow) {
        if (player == null) return;
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float shade = shadow ? 0.25F : 1.0F;
        GlStateManager.color(shade, shade, shade, (color >>> 24) / 255.0F);
        if (ChattingConfig.INSTANCE.getImprovedHeads()) {
            chatting$drawLayeredHead(x, y, ChattingConfig.INSTANCE.getCenterChatHeads() ? 0.5F : 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        boolean centered = ChattingConfig.INSTANCE.getCenterChatHeads();
        if (centered) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
        }
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y - 1, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y - 1, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
        if (centered) GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Renders the full head and outer skin model directly toward the chat camera. */
    @Unique
    private void chatting$drawLayeredHead(float x, float y, float centeredOffset) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 4.0F, y + 3.0F + centeredOffset, 0.0F);
        GlStateManager.scale(16.0F, -16.0F, 16.0F);
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        chatting$headModel.bipedHead.render(0.0625F);
        chatting$headModel.bipedHeadwear.render(0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

}
