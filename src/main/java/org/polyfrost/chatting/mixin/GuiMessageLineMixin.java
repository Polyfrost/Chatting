package org.polyfrost.chatting.mixin;

//? if >=26 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*//~ if =1.8.9 'net.minecraft.client.GuiMessage' -> 'net.minecraft.client.gui.ChatMessage'
import net.minecraft.client.GuiMessage;
*///?}
import net.minecraft.client.multiplayer.PlayerInfo;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//~ if =1.8.9 'GuiMessage.Line' -> 'ChatMessage'
@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin implements ChatLineHook {

    @Unique
    @Nullable
    private PlayerInfo chatting$playerInfo;

    @Unique
    private boolean chatting$headHidden;

    @Override
    @Nullable
    public PlayerInfo chatting$getPlayerInfo() {
        return chatting$playerInfo;
    }

    @Override
    public void chatting$setPlayerInfo(@Nullable PlayerInfo info) {
        this.chatting$playerInfo = info;
    }

    @Override
    public boolean chatting$isHeadHidden() {
        return chatting$headHidden;
    }

    @Override
    public void chatting$setHeadHidden(boolean hidden) {
        this.chatting$headHidden = hidden;
    }

    //? if >=26 {
    @Override
    @Nullable
    public GuiMessage chatting$getParent() {
        return ((GuiMessage.Line) (Object) this).parent();
    }

    @Override
    public void chatting$setParent(@Nullable GuiMessage parent) {
        // 26.1+ carries the parent natively on the line record so nothing to store
    }
    //?} else {
    /*@Unique
    @Nullable
    //~ if =1.8.9 'GuiMessage' -> 'ChatMessage'
    private GuiMessage chatting$parent;

    @Override
    @Nullable
    //~ if =1.8.9 'GuiMessage' -> 'ChatMessage'
    public GuiMessage chatting$getParent() {
        return chatting$parent;
    }

    @Override
    //~ if =1.8.9 'GuiMessage' -> 'ChatMessage'
    public void chatting$setParent(@Nullable GuiMessage parent) {
        this.chatting$parent = parent;
    }
    *///?}
    //? if = 1.8.9 {

    /*@Unique
    private boolean chatting$endOfEntry;

    @Override
    public boolean chatting$isEndOfEntry() {
        return chatting$endOfEntry;
    }

    @Override
    public void chatting$setEndOfEntry(boolean endOfEntry) {
        this.chatting$endOfEntry = endOfEntry;
    }
    *///?}
}
