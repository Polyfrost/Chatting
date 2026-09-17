package org.polyfrost.chatting;

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

/** Public default-adapter entrypoint for the modern client lifecycle owner. */
public final class ChattingEntrypoint implements ClientModInitializer {
    @Override
    public void initClient() {
        ChattingClient.INSTANCE.initialize();
    }
}
