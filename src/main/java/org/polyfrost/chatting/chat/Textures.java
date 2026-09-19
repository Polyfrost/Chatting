package org.polyfrost.chatting.chat;

import net.minecraft.util.ResourceLocation;

/** Shared resource identifiers for every native chat control. */
public final class Textures {
    public static final ResourceLocation COPY = texture("copy.png");
    public static final ResourceLocation DELETE = texture("delete.png");
    public static final ResourceLocation SCREENSHOT = texture("screenshot.png");
    public static final ResourceLocation SEARCH = texture("search.png");

    private Textures() {
    }

    private static ResourceLocation texture(String path) {
        return new ResourceLocation("chatting", path);
    }
}
