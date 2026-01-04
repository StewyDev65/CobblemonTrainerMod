package org.johnnymod.cobblemontest.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TrainerData implements SimpleResourceReloadListener<Map<String, TrainerDefinition>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerData.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, TrainerDefinition> TRAINERS = new HashMap<>();
    
    @Override
    public CompletableFuture<Map<String, TrainerDefinition>> load(ResourceManager manager, 
                                                                   ProfilerFiller profiler, 
                                                                   Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, TrainerDefinition> trainers = new HashMap<>();
            
            // Load all trainer JSON files from data/cobblemontest/trainers/
            manager.listResources("trainers", path -> path.getPath().endsWith(".json"))
                    .forEach((identifier, resource) -> {
                        try (Reader reader = new InputStreamReader(resource.open())) {
                            TrainerDefinition definition = GSON.fromJson(reader, TrainerDefinition.class);
                            String trainerName = identifier.getPath()
                                    .replace("trainers/", "")
                                    .replace(".json", "");
                            trainers.put(trainerName, definition);
                            LOGGER.info("Loaded trainer: {}", trainerName);
                        } catch (Exception e) {
                            LOGGER.error("Error loading trainer data from {}", identifier, e);
                        }
                    });
            
            return trainers;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Map<String, TrainerDefinition> data, 
                                         ResourceManager manager, 
                                         ProfilerFiller profiler, 
                                         Executor executor) {
        return CompletableFuture.runAsync(() -> {
            TRAINERS.clear();
            TRAINERS.putAll(data);
            LOGGER.info("Loaded {} trainers", TRAINERS.size());
        }, executor);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("cobblemontest", "trainers");
    }

    public static TrainerDefinition getTrainer(String name) {
        return TRAINERS.get(name);
    }

    public static Map<String, TrainerDefinition> getAllTrainers() {
        return new HashMap<>(TRAINERS);
    }

    public static class TrainerDefinition {
        private String displayName;
        private String skinId;
        
        public String getDisplayName() {
            return displayName;
        }

        public String getSkinId() {
            return skinId;
        }
    }
}
