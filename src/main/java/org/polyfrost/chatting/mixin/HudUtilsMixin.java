package org.polyfrost.chatting.mixin;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.hud.HUDUtils;
import cc.polyfrost.oneconfig.hud.Hud;
import org.polyfrost.chatting.chat.ChatHooks;
import org.polyfrost.chatting.config.ChattingConfig;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Mixin(HUDUtils.class)
public class HudUtilsMixin {

    @Unique
    private static boolean isChatWindow, isInputBox;

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/hud/Hud;setConfig(Lcc/polyfrost/oneconfig/config/Config;)V"), remap = false)
    private static void detect(Hud instance, Config config) {
        isChatWindow = instance.equals(ChattingConfig.INSTANCE.getChatWindow());
        isInputBox = instance.equals(ChattingConfig.INSTANCE.getChatInput());
        instance.setConfig(config);
    }

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"), remap = false)
    private static boolean paddingY(ArrayList instance, Object e) {
        BasicOption option = (BasicOption) e;
        if (isChatWindow || isInputBox) {
            ArrayList<BasicOption> removeQueue = new ArrayList<>();
            for (Object object : instance) {
                BasicOption basicOption = (BasicOption) object;
                List<String> shows = Arrays.asList("Show in F3 (Debug)", "Show in GUIs", "Enabled", "Position Alignment");
                if (basicOption.name.equals("Show in Chat") || (isInputBox && shows.contains(basicOption.name))) {
                    removeQueue.add(basicOption);
                }
                if (basicOption.name.equals("Input Field Draft")) {
                    basicOption.addListener(ChatHooks.INSTANCE::resetDraft);
                }
            }
            instance.removeAll(removeQueue);
        }
        List<String> paddings = Arrays.asList("X-Padding", "Y-Padding");
        if (isInputBox && paddings.contains(option.name)) return false;
        return instance.add(option);
    }

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/config/elements/BasicOption;addDependency(Ljava/lang/String;Ljava/util/function/Supplier;)V", ordinal = 5), remap = false)
    private static void no(BasicOption instance, String optionName, Supplier<Boolean> supplier) {
        if (isInputBox) return;
        instance.addDependency(optionName, supplier);
    }

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/config/elements/BasicOption;addDependency(Ljava/lang/String;Ljava/util/function/Supplier;)V", ordinal = 6), remap = false)
    private static void no1(BasicOption instance, String optionName, Supplier<Boolean> supplier) {
        if (isInputBox) return;
        instance.addDependency(optionName, supplier);
    }
}
