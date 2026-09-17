package org.polyfrost.chatting;

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

/** Public default-adapter entrypoint that delegates to Chatting's Kotlin singleton. */
public final class ChattingEntrypoint implements ClientModInitializer {
    @Override
    public void initClient() {
        Chatting.INSTANCE.initClient();
    }
}
