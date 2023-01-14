package cc.woverflow.chatting.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.libs.universal.UMouse;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.woverflow.chatting.Chatting;
import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.chatting.gui.components.CleanButton;
import cc.woverflow.chatting.hook.GuiNewChatHook;
import cc.woverflow.chatting.utils.ModCompatHooks;
import cc.woverflow.chatting.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.List;

@Mixin(value = GuiNewChat.class, priority = Integer.MIN_VALUE)
public abstract class GuiNewChatMixin extends Gui implements GuiNewChatHook {
    @Unique
    private int chatting$right = 0;
    @Unique
    private boolean chatting$isHovering;
    @Unique
    private boolean chatting$chatCheck;
    @Unique
    private int chatting$textOpacity;
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getLineCount();

    @Shadow
    private int scrollPos;

    @Shadow public abstract int getChatWidth();

    @Unique
    private static final ResourceLocation COPY = new ResourceLocation("chatting:copy.png");
    @Unique
    private static final ResourceLocation DELETE = new ResourceLocation("chatting:delete.png");

    /*?
    @Unique
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    @ModifyArg(method = "setChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;<init>(ILnet/minecraft/util/IChatComponent;I)V"))
    private IChatComponent handleAddDrawnLine(IChatComponent iChatComponent) {
        if (!ChattingConfig.INSTANCE.getShowTimestamp()) return iChatComponent;
        String time = " §7["+ sdf.format(new Date(System.currentTimeMillis())) + "]§r";
        iChatComponent.appendSibling(new ChatComponentText(time));
        return iChatComponent;
    }

     */

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void checkScreenshotKeybind(int j2, CallbackInfo ci) {
        if (Chatting.INSTANCE.getKeybind().isPressed()) {
            Chatting.INSTANCE.setDoTheThing(true);
        }
        chatting$chatCheck = false;
    }

    @ModifyVariable(method = "drawChat", at = @At("HEAD"), argsOnly = true)
    private int setUpdateCounterWhenYes(int updateCounter) {
        return Chatting.INSTANCE.getDoTheThing() ? 0 : updateCounter;
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), index = 2)
    private int setChatLimitWhenYes(int linesToDraw) {
        return Chatting.INSTANCE.getDoTheThing()
                ? GuiNewChat.calculateChatboxHeight(mc.gameSettings.chatHeightFocused) / 9
                : linesToDraw;
    }

    private boolean lineInBounds = false;

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_double(DDD)D"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableBlend()V")))
    private void captureDrawRect(Args args) {
        args.set(4, changeChatBackgroundColor(ChattingConfig.INSTANCE.getChatBackgroundColor(), args.get(4)));
        if (mc.currentScreen instanceof GuiChat) {
            int left = args.get(0);
            int top = args.get(1);
            int right = args.get(2);
            int bottom = args.get(3);
            if (isInBounds(left, top, right, bottom, getChatScale())) {
                chatting$isHovering = true;
                lineInBounds = true;
                args.set(4, changeChatBackgroundColor(ChattingConfig.INSTANCE.getHoveredChatBackgroundColor(), args.get(4)));
            }
        }
    }

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private void drawChatBox(Args args) {
        if (mc.currentScreen instanceof GuiChat) {
            float f = this.getChatScale();
            int left = 0;
            int top = (int) ((float) args.get(2) - 1);
            int right = MathHelper.ceiling_float_int((float)getChatWidth() / f) + 4;
            int bottom = (int) ((float) args.get(2) + 8);
            if ((chatting$isHovering && lineInBounds) || isInBounds(left, top, right, bottom, f)) {
                chatting$isHovering = true;
                drawCopyChatBox(right, top);
            }
        }
        lineInBounds = false;
    }

    private boolean isInBounds(int left, int top, int right, int bottom, float chatScale) {
        int mouseX = MathHelper.floor_double(UMouse.getScaledX()) - 3;
        int mouseY = MathHelper.floor_double(UMouse.getScaledY()) - 27 + ModCompatHooks.getYOffset() - ModCompatHooks.getChatPosition();
        mouseX = MathHelper.floor_float((float) mouseX / chatScale);
        mouseY = -(MathHelper.floor_float((float) mouseY / chatScale)); //WHY DO I NEED TO DO THIS
        return mouseX >= (left + ModCompatHooks.getXOffset()) && mouseY < bottom && mouseX < (right + 23 + ModCompatHooks.getXOffset()) && mouseY >= top;
    }

    private int changeChatBackgroundColor(OneColor color, int alphaColor) {
        return (((alphaColor >> 24) & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8) |
                ((color.getBlue() & 0xFF));
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), ordinal = 7)
    private int modifyYeah(int value) {
        return chatting$textOpacity = (int) (((float) (getChatOpen() ? 255 : value)) * (mc.gameSettings.chatOpacity * 0.9F + 0.1F));
    }
    /*/
    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void drawPre(int updateCounter, CallbackInfo ci) {
        RenderUtils.timestampPre();
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    private void drawPost(int updateCounter, CallbackInfo ci) {
        RenderUtils.timestampPost();
    }

     */

    @Inject(method = "drawChat", at = @At("RETURN"))
    private void checkStuff(int j2, CallbackInfo ci) {
        if (!chatting$chatCheck && chatting$isHovering) {
            chatting$isHovering = false;
        }
    }

    @Override
    public int getRight() {
        return chatting$right;
    }

    @Override
    public boolean isHovering() {
        return chatting$isHovering;
    }

    private void drawCopyChatBox(int right, int top) {
        chatting$chatCheck = true;
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
        chatting$right = right;
        drawModalRectWithCustomSizedTexture(right + 1, top, 0f, 0f, 9, 9, 9, 9);
        drawRect(right + 1, top, right + 10, top + 9, (((right + ModCompatHooks.getXOffset() + 3) <= (UMouse.getScaledX() / mc.ingameGUI.getChatGUI().getChatScale()) && (right + ModCompatHooks.getXOffset()) + 13 > (UMouse.getScaledX() / mc.ingameGUI.getChatGUI().getChatScale())) ? CleanButton.Companion.getHoveredColor() : CleanButton.Companion.getColor()));
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        mc.getTextureManager().bindTexture(DELETE);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawModalRectWithCustomSizedTexture(right + 11, top, 0f, 0f, 9, 9, 9, 9);
        drawRect(right + 11, top, right + 20, top + 9, (((right + ModCompatHooks.getXOffset() + 13) <= (UMouse.getScaledX() / mc.ingameGUI.getChatGUI().getChatScale()) && (right + ModCompatHooks.getXOffset()) + 23 > (UMouse.getScaledX() / mc.ingameGUI.getChatGUI().getChatScale())) ? CleanButton.Companion.getHoveredColor() : CleanButton.Companion.getColor()));
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public ChatLine getHoveredLine(int mouseY) {
        if (this.getChatOpen()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int k = mouseY / i - 27 + ModCompatHooks.getYOffset() - ModCompatHooks.getChatPosition();
            k = MathHelper.floor_float((float) k / f);

            if (k >= 0) {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        return this.drawnChatLines.get(i1);
                    }

                }
            }
        }
        return null;
    }

    @Override
    public Transferable getChattingChatComponent(int mouseY) {
        ChatLine subLine = getHoveredLine(mouseY);
        if (subLine != null) {
            ChatLine fullLine = this.getFullMessage(subLine);
            if (GuiScreen.isShiftKeyDown()) {
                if (fullLine != null) {
                    BufferedImage image = Chatting.INSTANCE.screenshotLine(fullLine);
                    if (image != null) RenderUtils.copyToClipboard(image);
                }
                return null;
            }
            ChatLine line = GuiScreen.isCtrlKeyDown() ? subLine : fullLine;
            String message = line == null ? "Could not find chat message." : line.getChatComponent().getFormattedText();
            String actualMessage = GuiScreen.isAltKeyDown() ? message : EnumChatFormatting.getTextWithoutFormattingCodes(message);
            Notifications.INSTANCE.send("Chatting", line == null ? "Could not find chat message." : "Copied following text: " + actualMessage);
            return new StringSelection(actualMessage);
        }
        return null;
    }

    @Override
    public int getTextOpacity() {
        return chatting$textOpacity;
    }
}
