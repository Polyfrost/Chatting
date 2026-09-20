package org.polyfrost.chatting.mixin;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.polyfrost.chatting.Chatting;
import org.polyfrost.chatting.chat.ChatScrolling;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/^* Routes the normal in-game mouse-wheel event to chat while Chat Peek is held. ^/
@Mixin(Minecraft.class)
public abstract class MinecraftMixin_ChatPeek {
    @Shadow public GuiIngame ingameGUI;

    @Redirect(
        method = "runTick",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I")
    )
    private int chatting$scrollPeekChat() {
        int wheel = Mouse.getEventDWheel();
        if (!Chatting.INSTANCE.getPeeking() || !ChattingConfig.INSTANCE.getPeekScrolling() || wheel == 0) return wheel;

        int amount = wheel > 0 ? 1 : -1;
        if (!GuiScreen.isShiftKeyDown()) amount *= 7;
        ingameGUI.getChatGUI().scroll(amount);
        ChatScrolling.INSTANCE.setShouldSmooth(true);
        return 0;
    }
}
*///?}
