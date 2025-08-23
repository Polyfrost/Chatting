package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.polyfrost.oneconfig.utils.color.ColorUtils;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.ChatWindow;
import org.polyfrost.chatting.config.ChattingConfig;
import org.polyfrost.chatting.hook.ChatHook;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.chatting.hook.GuiNewChatHook;
import org.polyfrost.chatting.utils.ModCompatHooks;
import org.polyfrost.chatting.utils.RenderUtils;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.List;

import static net.minecraft.client.gui.GuiNewChat.calculateChatboxHeight;
import static net.minecraft.client.gui.GuiNewChat.calculateChatboxWidth;

@Mixin(value = GuiNewChat.class, priority = 990)
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
    public abstract int getLineCount();
    @Shadow
    private int scrollPos;
    @Shadow
    public abstract int getChatWidth();
    @Unique
    private static final ResourceLocation chatting$COPY = new ResourceLocation("chatting:copy.png");
    @Unique
    private static final ResourceLocation chatting$DELETE = new ResourceLocation("chatting:delete.png");
    @Unique
    private boolean chatting$lineInBounds = false;
    @Unique
    private ChatLine chatting$chatLine;
    @Unique
    private int chatting$totalLines = 0;

    /*?
    @Unique
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    @ModifyArg(method = "setChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;<init>(ILnet/minecraft/util/IChatComponent;I)V"))
    private IChatComponent handleAddDrawnLine(IChatComponent iChatComponent) {
        if (!chatting$config().getShowTimestamp()) return iChatComponent;
        String time = " §7["+ sdf.format(new Date(System.currentTimeMillis())) + "]§r";
        iChatComponent.appendSibling(new ChatComponentText(time));
        return iChatComponent;
    }

     */

    @Unique
    private boolean chatting$lastOpen, chatting$closing;

    @Unique
    private long chatting$time;


    @Inject(method = "drawChat", at = @At("HEAD"))
    private void checkScreenshotKeybind(int j2, CallbackInfo ci) {
        if (Chatting.INSTANCE.getKeybind().isPressed()) {
            Chatting.INSTANCE.setDoTheThing(true);
        }
        chatting$chatCheck = false;
        if (chatting$lastOpen != getChatOpen()) {
            if (chatting$lastOpen) chatting$time = Minecraft.getSystemTime();
            chatting$lastOpen = getChatOpen();
        }
        long duration = chatting$config().getSmoothBG() ? (long) chatting$config().getBgDuration() : 0;
        chatting$closing = (Minecraft.getSystemTime() - chatting$time <= duration);
    }

    @Unique
    private int chatting$updateCounter;

    @ModifyVariable(method = "drawChat", at = @At("HEAD"), argsOnly = true)
    private int setUpdateCounterWhenYes(int updateCounter) {
        return (chatting$updateCounter = Chatting.INSTANCE.getDoTheThing() ? 0 : updateCounter);
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), index = 2)
    private int setChatLimitWhenYes(int linesToDraw) {
        return Chatting.INSTANCE.getDoTheThing()
                ? calculateChatboxHeight(mc.gameSettings.chatHeightFocused) / 9
                : linesToDraw;
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), ordinal = 0)
    private ChatLine captureChatLine(ChatLine chatLine) {
        chatting$chatLine = chatLine;
        return chatLine;
    }

    @Inject(method = "drawChat", at = @At(value = "HEAD"))
    private void startCaptureHeight(int updateCounter, CallbackInfo ci) {
        int i = this.getLineCount();
        chatting$totalLines = 0;
        List<ChatLine> list = ChatSearchingManager.filterMessages(ChatSearchingManager.INSTANCE.getLastSearch(), drawnChatLines);
        if (!list.isEmpty()) {
            for (int m = 0; m + this.scrollPos < list.size() && m < i; ++m) {
                ChatLine chatLine = list.get(m + this.scrollPos);
                int o = chatting$getOpacity(updateCounter, chatLine);
                if (o > 3) {
                    chatting$totalLines++;
                }
            }
        }
        chatting$config().getChatWindow().setHeight(chatting$totalLines * 9);
        chatting$config().getChatWindow().drawBG();
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
    private void startScissor(int updateCounter, CallbackInfo ci) {
        ChatWindow hud = chatting$config().getChatWindow();
        int mcScale = new ScaledResolution(mc).getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int height = (int) hud.getAnimationHeight();
        GL11.glScissor((int) ((hud.position.getX() - 3) * mcScale), mc.displayHeight - (int) (hud.position.getBottomY() + hud.getScale()) * mcScale, (int) ((hud.getAnimationWidth() + 3 + (ChattingConfig.INSTANCE.getExtendBG() ? 0 : 20)) * mcScale), (int) ((height + hud.getScale()) * mcScale));
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    private void disableScissor(int updateCounter, CallbackInfo ci) {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private void captureDrawRect(Args args, int updateCounter) {
        args.set(4, ColorUtils.getColor(0, 0, 0, 0));
        if (mc.currentScreen instanceof GuiChat) {
            int left = args.get(0);
            int top = args.get(1);
            int right = (int) args.get(2) + ModCompatHooks.getChatHeadOffset();
            args.set(2, right);
            if (chatting$isHovered(left, top, right - left, 9)) {
                chatting$isHovering = true;
                chatting$lineInBounds = true;
            }
            if (chatting$isHovered(left, top, right - left, 9) || chatting$isHovered(right + 1, top, 9, 9) || chatting$isHovered(right + 11, top, 9, 9)) {
                args.set(4, chatting$config().getHoveredChatBackgroundColor().getRGB());
            }
        }
    }

    @ModifyVariable(method = "drawChat", at = @At(value = "STORE", ordinal = 0), ordinal = 6)
    private int fadeTime(int value) {
        return value + 200 - (int) (chatting$config().getFadeTime() * 20);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatOpen()Z"))
    private boolean noFade(GuiNewChat instance) {
        ChatWindow hud = chatting$config().getChatWindow();
        return !chatting$config().getFade() || instance.getChatOpen() || (chatting$closing && hud.getAnimationHeight() - (hud.getHeight() + hud.getPaddingY() * 2) * hud.getScale() > 9 * hud.getScale());
    }

    @Unique
    private int chatting$getOpacity(int updateCounter, ChatLine chatLine) {
        if (chatLine != null) {
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            int n = updateCounter - chatLine.getUpdatedCounter() + 200 - (int) (chatting$config().getFadeTime() * 20);
            if (n < 200 || !chatting$config().getFade() || getChatOpen()) {
                double d = (double) n / 200.0;
                d = 1.0 - d;
                d *= 10.0;
                d = MathHelper.clamp_double(d, 0.0, 1.0);
                d *= d;
                int o = (int) (255 * d);
                if (!chatting$config().getFade() || getChatOpen()) {
                    o = 255;
                }
                o = (int) ((float) o * f);
                if (o <= 3) {
                    o = 0;
                }
                return o;
            } else {
                return 0;
            }
        }
        return Integer.MIN_VALUE;
    }

    @ModifyArgs(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private void drawChatBox(Args args) {
        if (mc.currentScreen instanceof GuiChat) {
            int left = 0;
            int top = (int) ((float) args.get(2) - 1);
            int right = MathHelper.ceiling_float_int((float) getChatWidth()) + 4 + ModCompatHooks.getChatHeadOffset();
            if ((chatting$isHovering && chatting$lineInBounds) || chatting$isHovered(left, top, right + 20, 9)) {
                chatting$isHovering = true;
                chatting$drawCopyChatBox(right, top);
            }
        }
        chatting$lineInBounds = false;
    }

    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void captureComponent(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHook.currentLine = new ChatLine(0, chatComponent, 0);
    }

    @Inject(method = "setChatLine", at = @At(value = "NEW", target = "net/minecraft/client/gui/ChatLine", ordinal = 0))
    private void markVisible(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHook.lineVisible = true;
    }

    @Inject(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void markVisible2(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHook.lineVisible = false;
    }

    @ModifyVariable(method = "setChatLine", at = @At(value = "STORE"), ordinal = 2)
    private int wrap(int value) {
        return ChattingConfig.INSTANCE.getChatWindow().getCustomChatWidth() ? Chatting.INSTANCE.getChatWidth() : calculateChatboxWidth(mc.gameSettings.chatWidth);
    }

    @Unique
    private boolean chatting$isHovered(int x, int y, int width, int height) {
        ChatWindow hud = chatting$config().getChatWindow();
        ScaledResolution scaleResolution = new ScaledResolution(mc);
        int scale = scaleResolution.getScaleFactor();
        int mouseX = Mouse.getX();
        int mouseY = mc.displayHeight - Mouse.getY();
        int actualX = (int) (((int) hud.position.getX() + (x + hud.getPaddingX()) * hud.getScale()) * scale);
        int actualY = (int) (((int) hud.position.getBottomY() + (y - hud.getPaddingY()) * hud.getScale()) * scale);
        return mouseX > actualX && mouseX < actualX + width * hud.getScale() * scale && mouseY > actualY && mouseY < actualY + height * hud.getScale() * scale;
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), ordinal = 0)
    private double modifyYeah(double value) {
        chatting$textOpacity = chatting$getOpacity(chatting$updateCounter, chatting$chatLine);
        if (chatting$textOpacity == Integer.MIN_VALUE) {
            chatting$textOpacity = 0;
        }
        return chatting$closing ? 1 : value;
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

    @Inject(method = "getChatScale", at = @At("HEAD"), cancellable = true)
    private void chatScale(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ChattingConfig.INSTANCE.getChatWindow().getScale());
    }

    @Override
    public int chatting$getRight() {
        return chatting$right;
    }

    @Override
    public boolean chatting$isHovering() {
        return chatting$isHovering;
    }

    @Unique
    private void chatting$drawCopyChatBox(int right, int top) {
        chatting$chatCheck = true;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        int posLeft = right + 1;
        int posRight = right + 10;
        if (!ChattingConfig.INSTANCE.getExtendBG()) {
            GlStateManager.translate(chatting$config().getChatWindow().getPaddingX() * chatting$config().getChatWindow().getScale(), 0f, 0f);
        }
        if (chatting$config().getChatCopy()) {
            mc.getTextureManager().bindTexture(chatting$COPY);
            chatting$right = right;
            boolean hovered = chatting$isHovered(posLeft, top, posRight - posLeft, 9);
            OneColor color = hovered ? chatting$config().getChatButtonHoveredBackgroundColor() : chatting$config().getChatButtonBackgroundColor();
            drawRect(posLeft, top, posRight, top + 9, color.getRGB());
            color = hovered ? chatting$config().getChatButtonHoveredColor() : chatting$config().getChatButtonColor();
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (chatting$config().getButtonShadow()) {
                GlStateManager.color(0f, 0f, 0f, color.getAlpha() / 255f);
                drawModalRectWithCustomSizedTexture(posLeft + 1, top + 1, 0f, 0f, 9, 9, 9, 9);
            }
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            drawModalRectWithCustomSizedTexture(posLeft, top, 0f, 0f, 9, 9, 9, 9);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            posLeft += 10;
            posRight += 10;
        }
        if (chatting$config().getChatDelete()) {
            mc.getTextureManager().bindTexture(chatting$DELETE);
            boolean hovered = chatting$isHovered(posLeft, top, posRight - posLeft, 9);
            OneColor color = hovered ? chatting$config().getChatButtonHoveredBackgroundColor() : chatting$config().getChatButtonBackgroundColor();
            drawRect(posLeft, top, posRight, top + 9, color.getRGB());
            color = hovered ? chatting$config().getChatButtonHoveredColor() : chatting$config().getChatButtonColor();
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (chatting$config().getButtonShadow()) {
                GlStateManager.color(0f, 0f, 0f, color.getAlpha() / 255f);
                drawModalRectWithCustomSizedTexture(posLeft + 1, top + 1, 0f, 0f, 9, 9, 9, 9);
            }
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            drawModalRectWithCustomSizedTexture(posLeft, top, 0f, 0f, 9, 9, 9, 9);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public ChatLine chatting$getHoveredLine(int mouseY) {
        if (this.getChatOpen()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            ChatWindow hud = chatting$config().getChatWindow();
            float f = hud.getScale();
            int k = (int) (mouseY / i - (scaledresolution.getScaledHeight() - hud.position.getBottomY() + hud.getPaddingY() * hud.getScale()) + ModCompatHooks.getYOffset());
            k = MathHelper.floor_float((float) k / f);

            if (k >= 0) {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        List<ChatLine> m = ChatSearchingManager.filterMessages(ChatSearchingManager.INSTANCE.getLastSearch(), this.drawnChatLines);
                        return m != null ? m.get(i1) : null;
                    }

                }
            }
        }
        return null;
    }

    @Override
    public Transferable chatting$getChattingChatComponent(int mouseY, int mouseButton) {
        ChatLine subLine = chatting$getHoveredLine(mouseY);
        if (subLine != null) {
            ChatLine fullLine = ((ChatLineHook) subLine).chatting$getFullMessage();
            if (GuiScreen.isShiftKeyDown() && mouseButton == 0) {
                if (fullLine != null) {
                    BufferedImage image = Chatting.INSTANCE.screenshotLine(subLine);
                    if (image != null) RenderUtils.copyToClipboard(image);
                }
                return null;
            }
            ChatLine line = GuiScreen.isCtrlKeyDown() && mouseButton == 0 ? subLine : fullLine;
            String message = line == null ? "Could not find chat message." : line.getChatComponent().getFormattedText();
            String actualMessage = GuiScreen.isAltKeyDown() ? message : EnumChatFormatting.getTextWithoutFormattingCodes(message);
            Notifications.INSTANCE.send("Chatting", line == null ? "Could not find chat message." : "Copied following text: " + actualMessage);
            return new StringSelection(actualMessage);
        }
        return null;
    }

    @Unique
    private ChattingConfig chatting$config() {
        return ChattingConfig.INSTANCE;
    }

    @Override
    public int chatting$getTextOpacity() {
        return chatting$textOpacity;
    }
}
