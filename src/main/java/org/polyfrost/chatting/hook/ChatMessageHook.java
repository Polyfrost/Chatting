package org.polyfrost.chatting.hook;

public interface ChatMessageHook {

    /** The wall-clock arrival time of this message in epoch milliseconds, or {@code -1} if unset. */
    long chatting$getTimestamp();

    void chatting$setTimestamp(long millis);
}
