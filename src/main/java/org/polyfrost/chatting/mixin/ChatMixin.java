package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.NotNull;
import org.polyfrost.chatting.core.McChat;
import org.polyfrost.chatting.hook.ChatHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Collection;
import java.util.List;

//#if MC == 1.16.5
//$$ import net.minecraft.text.Text;
//#endif

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class ChatMixin implements ChatHook {

    @Shadow
    @Final
    private List<
            //#if MC == 11605
            //$$ ChatHudLine<Text>
            //#else
            ChatHudLine
            //#endif
            > messages;

    //#if MC > 1.16.5
    @ModifyArgs(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V"))
    //#else
    //$$ @ModifyArgs(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1))
    //#endif
    private void onAdd(Args args) {
        McChat.INSTANCE.addMessage(args.get(1));
    }

    //#if MC > 1.16.5
    @Inject(method = "restoreChatState", at = @At(value = "TAIL"))
    private void onRestore(ChatHud.ChatState chatState, CallbackInfo ci) {
        McChat.INSTANCE.refreshChat();
    }

    @Inject(method = "queueForRemoval", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;refresh()V"))
    private void onRemoval(net.minecraft.network.message.MessageSignatureData messageSignatureData, CallbackInfoReturnable<Object> cir) {
        McChat.INSTANCE.refreshChat();
    }
    //#else
    //$$ @Inject(method = "removeMessage", at = @At("HEAD"))
    //$$ private void onRemoval(int i, CallbackInfo ci) {
    //$$     McChat.INSTANCE.removeMessageById(i);
    //$$ }
    //#endif

    @Inject(method = "clear", at = @At("HEAD"))
    private void onClear(CallbackInfo ci) {
        McChat.INSTANCE.clearMessages();
    }

    @Override
    public void chatting$deleteChatLine(
            //#if MC == 1.16.5
            //$$ @NotNull Collection<? extends @NotNull ChatHudLine<@NotNull Text>>
            //#elseif MC < 1.16.5
            //$$ @NotNull Collection<? extends @NotNull ChatHudLine>
            //#else
            @NotNull Collection<@NotNull ChatHudLine>
            //#endif
            chatLines
    ) {
        this.messages.removeAll(chatLines);
    }
}
