package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.core.ConfigUtils;
import cc.polyfrost.oneconfig.config.elements.*;
import cc.polyfrost.oneconfig.hud.*;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import net.minecraft.client.Minecraft;
import org.polyfrost.chatting.chat.*;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = HUDUtils.class, remap = false)
public class HUDUtilsMixin {

    @Inject(method = "addHudOptions", at = @At("TAIL"))
    private static void hudUtils$modifyOptions(OptionPage page, Field field, Object instance, Config config, CallbackInfo ci) {
        Hud hud = (Hud) ConfigUtils.getField(field, instance);
        if (!(hud instanceof ChatWindow) && !(hud instanceof ChatInputBox)) return;
        HUD hudAnnotation = field.getAnnotation(HUD.class);
        HudCore.hudOptions.removeIf(HUDUtilsMixin::hudUtils$shouldRemove);
        ConfigUtils.getSubCategory(page, hudAnnotation.category(), hudAnnotation.subcategory()).options.removeIf(HUDUtilsMixin::hudUtils$shouldRemove);
    }

    private static boolean hudUtils$shouldRemove(BasicOption option) {
        String fieldName = option.getField().getName();
        Object hud = option.getParent();
        boolean isChatWindow = hud instanceof ChatWindow;
        boolean isInputBox = hud instanceof ChatInputBox;
        if (!isChatWindow && !isInputBox) return false;
        switch (fieldName) {
            case "showInChat":
                return true;
            case "enabled":
                if (isInputBox) try {
                    option.getField().set(hud, true);
                } catch (Exception ignored) {
                }
            case "paddingX":
            case "paddingY":
            case "showInGuis":
            case "showInDebug":
            case "positionAlignment":
            case "scale":
            case "locked":
            case "ignoreCaching":
            case "resetPosition":
                if (isInputBox) return true;
                break;
            case "inputFieldDraft":
                option.addListener(ChatHooks.INSTANCE::resetDraft);
                break;
            case "focusedHeight":
            case "unfocusedHeight":
                option.addDependency("Custom Chat Height", () -> ChattingConfig.INSTANCE.getChatWindow().getCustomChatHeight());
                option.addListener(() -> Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat());
                break;
            case "customWidth":
                option.addDependency("Custom Chat Width", () -> ChattingConfig.INSTANCE.getChatWindow().getCustomChatWidth());
                option.addListener(() -> Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat());
                break;
        }

        return false;
    }

}
