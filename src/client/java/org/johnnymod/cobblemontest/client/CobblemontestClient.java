package org.johnnymod.cobblemontest.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.johnnymod.cobblemontest.Cobblemontest;

public class CobblemontestClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Cobblemontest.LOGGER.info("Initializing Cobblemon Test client");

        // Register entity renderers
        ModEntityRenderers.initialize();

        Cobblemontest.LOGGER.info("Cobblemon Test client initialized!");
    }
}
