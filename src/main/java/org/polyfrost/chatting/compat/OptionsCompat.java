package org.polyfrost.chatting.compat;

//? if = 1.8.9 {
/*import net.minecraft.client.Options;

import java.util.function.Supplier;

public interface OptionsCompat {
    default Supplier<Float> chatScale() {
        return () -> ((Options) (Object) this).chatScale;
    }

    default Supplier<Float> chatWidth() {
        return () -> ((Options) (Object) this).chatWidth;
    }

    default Supplier<Float> chatHeightFocused() {
        return () -> ((Options) (Object) this).focusedChatHeight;
    }

    default Supplier<Float> chatHeightUnfocused() {
        return () -> ((Options) (Object) this).unfocusedChatHeight;
    }

    default Supplier<Float> chatOpacity() {
        return () -> ((Options) (Object) this).chatOpacity;
    }
}
*///?}
