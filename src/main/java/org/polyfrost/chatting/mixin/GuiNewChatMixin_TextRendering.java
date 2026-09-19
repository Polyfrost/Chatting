package org.polyfrost.chatting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
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
public abstract class GuiNewChatMixin_TextRendering {
    @Unique private static final float chatting$shadowAlpha = 0.15F;
    @Shadow @Final private Minecraft mc;
    @Shadow public abstract float getChatScale();
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
            chatting$drawHead(player, x, y);
        }
        return renderer.drawString(text, textX, y, color, ChattingConfig.INSTANCE.getTextRenderType() != 0);
    }

    @Unique
    private void chatting$drawHead(NetworkPlayerInfo player, float x, float y) {
        if (player == null) return;

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        // A layered ModelPlayer head needs depth testing so its front faces
        // occlude the back of the cube.  Keep this common for flat and layered
        // heads; the GUI depth buffer is clear before the chat overlay draws.
        GlStateManager.enableDepth();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        // The model path samples the lightmap even with GUI lighting disabled.
        // Use vanilla full-bright coordinates so a head never inherits world light.
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        // Chat has already been scaled by getChatScale().  A sub-pixel shadow
        // becomes ragged at other scales, so retain the optional one-pixel pass
        // only for the unscaled chat layout.
        if (ChattingConfig.INSTANCE.getChatHeadShadow() != 0 && getChatScale() == 1.0F) {
            GlStateManager.depthMask(false);
            GlStateManager.resetColor();
            GlStateManager.color(0.0F, 0.0F, 0.0F, chatting$shadowAlpha);
            chatting$drawHeadGeometry(player, x + 1.0F, y + 1.0F);
        }

        GlStateManager.depthMask(true);
        // Chat heads intentionally do not receive the line's fade alpha.
        // resetColor is required here: GUI draws and other HUD mods can change
        // the real OpenGL color without updating GlStateManager's cache.  With
        // no shadow pass, a cached-white color would otherwise leave a stale
        // (often transparent) color active for the only head draw.
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        chatting$drawHeadGeometry(player, x, y);

        // Restore drawChat's pre-font-renderer state rather than leaking model
        // lighting/depth or the shadow's alpha into the remaining chat lines.
        GlStateManager.depthMask(true);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
    }

    @Unique
    private void chatting$drawHeadGeometry(NetworkPlayerInfo player, float x, float y) {
        GlStateManager.pushMatrix();
        // The default top is the line-box top (y - 1); centering on the
        // eight-pixel glyphs moves it to y, rather than centering on the backdrop.
        if (ChattingConfig.INSTANCE.getCenterChatHeads()) GlStateManager.translate(0.0F, 1.0F, 0.0F);
        if (ChattingConfig.INSTANCE.getImprovedHeads()) chatting$drawLayeredHead(x, y);
        else chatting$drawTabHead(player, x, y);
        GlStateManager.popMatrix();
    }

    /** The normal face follows GuiPlayerTabOverlay's tested 1.8.9 quad recipe. */
    @Unique
    private void chatting$drawTabHead(NetworkPlayerInfo player, float x, float y) {
        EntityPlayer entity = mc.theWorld == null ? null : mc.theWorld.getPlayerEntityByUUID(player.getGameProfile().getId());
        boolean upsideDown = entity != null
            && entity.isWearing(EnumPlayerModelParts.CAPE)
            && ("Dinnerbone".equals(player.getGameProfile().getName()) || "Grumm".equals(player.getGameProfile().getName()));
        int faceV = 8 + (upsideDown ? 8 : 0);
        int faceHeight = 8 * (upsideDown ? -1 : 1);

        // Preserve fractional positions despite the quad helper's integer coordinates.
        GlStateManager.translate(x, y - 1.0F, 0.0F);
        Gui.drawScaledCustomSizeModalRect(0, 0, 8.0F, (float) faceV, 8, faceHeight, 8, 8, 64.0F, 64.0F);
        if (entity != null && entity.isWearing(EnumPlayerModelParts.HAT)) {
            Gui.drawScaledCustomSizeModalRect(0, 0, 40.0F, (float) faceV, 8, faceHeight, 8, 8, 64.0F, 64.0F);
        }
    }

    /** Renders the full head and outer skin model directly toward the chat camera. */
    @Unique
    private void chatting$drawLayeredHead(float x, float y) {
        // The scale and X-axis half-turn leave model Y -8..0 increasing downward.
        // Anchor at y + 7 to match the tab-head bounds from y - 1 through y + 7,
        // before the shared centering offset.
        GlStateManager.translate(x + 4.0F, y + 7.0F, 0.0F);
        GlStateManager.scale(16.0F, -16.0F, 16.0F);
        // ModelRenderer's unrotated cube faces away from the 2D chat camera,
        // and the inverted GUI Y scale would put its chin above its hair. An
        // X-axis half-turn corrects both axes while retaining a straight-on
        // view of the front skin and raised headwear layer.
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableCull();
        chatting$headModel.bipedHead.render(0.0625F);
        chatting$headModel.bipedHeadwear.render(0.0625F);
        GlStateManager.enableCull();
    }

}
