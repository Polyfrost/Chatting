package cc.woverflow.chattils.mixin;

import cc.woverflow.chattils.chat.ChatTabs;
import cc.woverflow.chattils.config.ChattilsConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String handleSentMessages(String value) {
        if (value.startsWith("/")) return value;
        if (ChattilsConfig.INSTANCE.getChatTabs() && ChatTabs.INSTANCE.getCurrentTab() != null && !ChatTabs.INSTANCE.getCurrentTab().getPrefix().isEmpty()) {
            return ChatTabs.INSTANCE.getCurrentTab().getPrefix() + value;
        } else {
            return value;
        }
    }
}