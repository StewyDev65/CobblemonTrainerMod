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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TrainerData implements SimpleResourceReloadListener<Map<String, TrainerData.TrainerDefinition>> {

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
                            LOGGER.info("Loaded trainer: {} with {} Pokemon", trainerName,
                                    definition.pokemon != null ? definition.pokemon.size() : 0);
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

    /**
     * Represents a trainer definition loaded from JSON.
     * Contains display information and Pokemon team data.
     */
    public static class TrainerDefinition {
        private String displayName;
        private String skinId;
        private List<PokemonData> pokemon;

        public String getDisplayName() {
            return displayName;
        }

        public String getSkinId() {
            return skinId;
        }

        public List<PokemonData> getPokemon() {
            return pokemon;
        }
    }

    /**
     * Represents Pokemon data for a trainer's team.
     * Can be expanded to include more properties like moves, abilities, IVs, etc.
     */
    public static class PokemonData {
        private String species;
        private int level = 5;  // Default level
        private String ability;
        private List<String> moves;
        private String nature;
        private String heldItem;
        private boolean shiny = false;
        private String form;

        // IVs and EVs for more advanced configuration
        private Integer ivHp;
        private Integer ivAttack;
        private Integer ivDefense;
        private Integer ivSpecialAttack;
        private Integer ivSpecialDefense;
        private Integer ivSpeed;

        public String getSpecies() {
            return species;
        }

        public int getLevel() {
            return level;
        }

        public String getAbility() {
            return ability;
        }

        public List<String> getMoves() {
            return moves;
        }

        public String getNature() {
            return nature;
        }

        public String getHeldItem() {
            return heldItem;
        }

        public boolean isShiny() {
            return shiny;
        }

        public String getForm() {
            return form;
        }

        public Integer getIvHp() { return ivHp; }
        public Integer getIvAttack() { return ivAttack; }
        public Integer getIvDefense() { return ivDefense; }
        public Integer getIvSpecialAttack() { return ivSpecialAttack; }
        public Integer getIvSpecialDefense() { return ivSpecialDefense; }
        public Integer getIvSpeed() { return ivSpeed; }

        /**
         * Converts this PokemonData to a Cobblemon properties string.
         * This can be parsed by PokemonProperties.parse() to create a Pokemon.
         */
        public String toPropertiesString() {
            StringBuilder sb = new StringBuilder();
            sb.append(species);

            if (level > 0) {
                sb.append(" level=").append(level);
            }

            if (shiny) {
                sb.append(" shiny=true");
            }

            if (form != null && !form.isEmpty()) {
                sb.append(" form=").append(form);
            }

            if (nature != null && !nature.isEmpty()) {
                sb.append(" nature=").append(nature);
            }

            if (ability != null && !ability.isEmpty()) {
                sb.append(" ability=").append(ability);
            }

            return sb.toString();
        }
    }
}