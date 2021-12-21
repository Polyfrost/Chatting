package com.raeids.stratus.mixin;

import com.raeids.stratus.Stratus;
import com.raeids.stratus.hook.ChatSearchingKt;
import com.raeids.stratus.hook.ChatTabs;
import com.raeids.stratus.hook.GuiIngameForgeHook;
import com.raeids.stratus.hook.GuiNewChatHook;
import gg.essential.universal.UResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin extends Gui implements GuiNewChatHook {
    @Unique private int stratus$x = 0;
    @Unique private int stratus$y = 0;
    @Unique private boolean stratus$shouldCopy;
    @Unique private ChatLine stratus$chatLine;
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private List<ChatLine> drawnChatLines;
    @Unique private static final ResourceLocation COPY = new ResourceLocation("stratus:copy.png");

    @Inject(method = "setChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_float(F)I", shift = At.Shift.AFTER))
    private void setDoing(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatTabs.INSTANCE.setDoing(true);
    }

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void checkScreenshotKeybind(int j2, CallbackInfo ci) {
        if (Stratus.INSTANCE.getKeybind().isPressed()) {
            Stratus.INSTANCE.setDoTheThing(true);
        }
    }

    @ModifyVariable(method = "drawChat", at = @At("HEAD"), argsOnly = true)
    private int setUpdateCounterWhjenYes(int updateCounter) {
        return Stratus.INSTANCE.getDoTheThing() ? 0 : updateCounter;
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), index = 2)
    private int setChatLimitWhenYes(int linesToDraw) {
        return Stratus.INSTANCE.getDoTheThing()
                ? GuiNewChat.calculateChatboxHeight(mc.gameSettings.chatHeightFocused) / 9
                : linesToDraw;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object captureChatLine(List<ChatLine> instance, int i) {
        stratus$chatLine = instance.get(i);
        return stratus$chatLine;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_double(DDD)D"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableBlend()V")))
    private void captureDrawRect(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, bottom, color);
        final int k1 = Mouse.getX() * UResolution.getScaledWidth() / this.mc.displayWidth;
        final int l1 = UResolution.getScaledHeight() - Mouse.getY() * UResolution.getScaledHeight() / this.mc.displayHeight - 1;
        int mouseX = k1 - ((GuiIngameForgeHook) mc.ingameGUI).getX() - 2;
        int mouseY = l1 - ((GuiIngameForgeHook) mc.ingameGUI).getY() - 20;
        if (mouseX >= left && mouseY <= bottom && mouseX - 9 <= right && mouseY >= top) {
            stratus$shouldCopy = true;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            mc.getTextureManager().bindTexture(COPY);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            stratus$x = right;
            stratus$y = top;
            Gui.drawModalRectWithCustomSizedTexture(right, top, 0f, 0f, 9, 9, 9, 9);
            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
        } else if (stratus$shouldCopy) {
            stratus$shouldCopy = false;
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiNewChat;drawnChatLines:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<ChatLine> injected(GuiNewChat instance) {
        return ChatSearchingKt.filterMessages(drawnChatLines);
    }

    @Override
    public int getX() {
        return stratus$x;
    }

    @Override
    public int getY() {
        return stratus$y;
    }

    @Override
    public boolean shouldCopy() {
        return stratus$shouldCopy;
    }

    @Override
    public String copyString() {
        return stratus$chatLine.getChatComponent().getFormattedText();
    }
}
