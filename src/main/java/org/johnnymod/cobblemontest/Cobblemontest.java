package org.johnnymod.cobblemontest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import org.johnnymod.cobblemontest.command.TrainerSpawnCommand;
import org.johnnymod.cobblemontest.data.TrainerData;
import org.johnnymod.cobblemontest.entity.ModEntities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cobblemontest implements ModInitializer {

    public static final String MOD_ID = "cobblemontest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Cobblemon Test Trainer Mod");

        // Initialize entities
        ModEntities.initialize();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TrainerSpawnCommand.register(dispatcher);
        });

        // Register resource reload listener for trainer data
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TrainerData());

        LOGGER.info("Cobblemon Test Trainer Mod initialized!");
    }
}