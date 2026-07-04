package org.polyfrost.chatting.chat;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public final class Textures {

    //? if >=1.21.11 {
    /*public static final Identifier COPY = Identifier.fromNamespaceAndPath("chatting", "copy.png");
    public static final Identifier DELETE = Identifier.fromNamespaceAndPath("chatting", "delete.png");
    public static final Identifier SCREENSHOT = Identifier.fromNamespaceAndPath("chatting", "screenshot.png");
    public static final Identifier SEARCH = Identifier.fromNamespaceAndPath("chatting", "search.png");
    *///?} else {
    public static final ResourceLocation COPY = ResourceLocation.fromNamespaceAndPath("chatting", "copy.png");
    public static final ResourceLocation DELETE = ResourceLocation.fromNamespaceAndPath("chatting", "delete.png");
    public static final ResourceLocation SCREENSHOT = ResourceLocation.fromNamespaceAndPath("chatting", "screenshot.png");
    public static final ResourceLocation SEARCH = ResourceLocation.fromNamespaceAndPath("chatting", "search.png");
    //?}

    private Textures() {
    }
}
