package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chatting.chat.ChatSearchingManager;
import org.polyfrost.chatting.chat.SmoothChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/^*
 * 1.8.9 render adapter for Chatting's modern smooth-message state machine.
 ^/
@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_SmoothChat {
    @Shadow private boolean isScrolled;
    @Shadow public abstract float getChatScale();
    @Unique private int chatting$newLines = -1;
    @Unique private int chatting$lineBeingDrawn;

    // Run only after tab filtering has accepted the message. Starting at HEAD
    // animated all visible chat for packets which never produced a line.
    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("RETURN"))
    private void chatting$startMessageAnimation(IChatComponent component, int chatLineId, CallbackInfo ci) {
        if (!ChatSearchingManager.matches(ChatSearchingManager.INSTANCE.getLastSearch(),
            EnumChatFormatting.getTextWithoutFormattingCodes(component.getUnformattedText()))) return;
        SmoothChat.INSTANCE.start();
    }

    @Inject(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void chatting$translateNewMessage(int updateCounter, CallbackInfo ci) {
        GlStateManager.translate(0f, SmoothChat.INSTANCE.translateY(isScrolled, getChatScale()), 0f);
    }

    @ModifyArg(
        method = "drawChat",
        at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false),
        index = 0
    )
    private int chatting$captureLineBeingDrawn(int line) {
        chatting$lineBeingDrawn = line;
        return line;
    }

    @ModifyArg(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"
        ),
        index = 3
    )
    private int chatting$fadeNewMessage(int color) {
        return chatting$lineBeingDrawn <= chatting$newLines ? SmoothChat.INSTANCE.fadeColor(color) : color;
    }

    // splitText returns one element per visual line. Only that newly inserted
    // prefix should fade; older visible rows must remain fully opaque.
    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<IChatComponent> chatting$captureNewLines(List<IChatComponent> lines) {
        chatting$newLines = lines.size() - 1;
        return lines;
    }
}
*///?}
