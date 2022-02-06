package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.utils.RenderUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.wyvest.redaction.features.NameHighlight")
public class RedactionNameHighlightMixin {

    @Dynamic("REDACTION")
    @Inject(method = "highlightName", at = @At("HEAD"), cancellable = true)
    private static void onNameHighlight(String text, CallbackInfoReturnable<String> cir) {
        if (RenderUtils.getBypassNameHighlight()) {
            cir.setReturnValue(text);
        }
    }
}
