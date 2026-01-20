package org.johnnymod.cobblemontest.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.InteractionHand;
import org.johnnymod.cobblemontest.Cobblemontest;
import org.johnnymod.cobblemontest.client.gui.TrainerCardScreen;
import org.johnnymod.cobblemontest.item.ModItems;

public class CobblemontestClient implements ClientModInitializer {

    private static int useItemCooldown = 0;

    @Override
    public void onInitializeClient() {
        Cobblemontest.LOGGER.info("Initializing Cobblemon Test client");

        // Register entity renderers
        ModEntityRenderers.initialize();
        
        // Register client tick handler for trainer card right-click detection
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (useItemCooldown > 0) {
                useItemCooldown--;
            }
            
            if (client.player != null && client.screen == null) {
                // Check if player is actively using an item (right-click held)
                if (client.options.keyUse.isDown() && useItemCooldown == 0) {
                    var mainHand = client.player.getItemInHand(InteractionHand.MAIN_HAND);
                    var offHand = client.player.getItemInHand(InteractionHand.OFF_HAND);
                    
                    if (mainHand.is(ModItems.TRAINER_CARD) || offHand.is(ModItems.TRAINER_CARD)) {
                        client.setScreen(new TrainerCardScreen());
                        useItemCooldown = 10; // Prevent spam-opening
                    }
                }
            }
        });

        Cobblemontest.LOGGER.info("Cobblemon Test client initialized!");
    }
}