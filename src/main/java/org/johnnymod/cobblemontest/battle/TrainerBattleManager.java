package org.johnnymod.cobblemontest.battle;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.johnnymod.cobblemontest.Cobblemontest;
import org.johnnymod.cobblemontest.data.TrainerData;
import org.johnnymod.cobblemontest.entity.TrainerEntity;

import java.util.*;

/**
 * Manages trainer battles - handles initialization, Pokemon spawning, and battle lifecycle.
 * Designed to be modular for future expansion (multiple Pokemon, items, etc.)
 */
public class TrainerBattleManager {

    private static final TrainerBattleManager INSTANCE = new TrainerBattleManager();

    // Track active battles by trainer UUID
    private final Map<UUID, TrainerBattleSession> activeBattles = new HashMap<>();

    private TrainerBattleManager() {}

    public static TrainerBattleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Attempts to start a battle between a player and trainer.
     * @param player The player initiating the battle
     * @param trainer The trainer entity being challenged
     * @return true if battle started successfully, false otherwise
     */
    public boolean startBattle(ServerPlayer player, TrainerEntity trainer) {
        // Check if player is already in a battle
        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            player.sendSystemMessage(Component.literal("You are already in a battle!"));
            return false;
        }

        // Check if trainer is already in a battle
        if (isTrainerInBattle(trainer)) {
            player.sendSystemMessage(Component.literal(trainer.getTrainerName() + " is already in a battle!"));
            return false;
        }

        // Get trainer's Pokemon data
        TrainerData.TrainerDefinition definition = TrainerData.getTrainer(
                getTrainerIdFromEntity(trainer)
        );

        if (definition == null || definition.getPokemon() == null || definition.getPokemon().isEmpty()) {
            player.sendSystemMessage(Component.literal("This trainer has no Pokemon to battle with!"));
            return false;
        }

        // Check if player has Pokemon
        var playerParty = Cobblemon.INSTANCE.getStorage().getParty(player);
        if (playerParty.toGappyList().stream().allMatch(Objects::isNull)) {
            player.sendSystemMessage(Component.literal("You don't have any Pokemon!"));
            return false;
        }

        // Check if player has any Pokemon that can battle (not all fainted)
        boolean hasAlivePokemon = playerParty.toGappyList().stream()
                .filter(Objects::nonNull)
                .anyMatch(pokemon -> pokemon.getCurrentHealth() > 0);

        if (!hasAlivePokemon) {
            player.sendSystemMessage(Component.literal("All your Pokemon have fainted!"));
            return false;
        }

        // Create all Pokemon for the trainer's team
        List<Pokemon> trainerPokemonList = new ArrayList<>();
        for (TrainerData.PokemonData pokemonData : definition.getPokemon()) {
            Pokemon pokemon = createPokemonFromData(pokemonData);
            if (pokemon == null) {
                player.sendSystemMessage(Component.literal("Failed to create trainer's Pokemon!"));
                Cobblemontest.LOGGER.error("Failed to create Pokemon " + pokemonData.getSpecies() +
                        " for trainer: " + trainer.getTrainerName());
                return false;
            }
            trainerPokemonList.add(pokemon);
        }

        // Start the battle - Cobblemon will handle Pokemon entity spawning with animations
        return startTrainerBattle(player, trainer, trainerPokemonList);
    }

    /**
     * Creates a Pokemon from trainer data.
     */
    private Pokemon createPokemonFromData(TrainerData.PokemonData data) {
        try {
            // Build Pokemon properties string
            StringBuilder propertiesString = new StringBuilder();
            propertiesString.append(data.getSpecies());

            if (data.getLevel() > 0) {
                propertiesString.append(" level=").append(data.getLevel());
            }

            // Parse and create the Pokemon
            PokemonProperties properties = PokemonProperties.Companion.parse(propertiesString.toString(), " ", "=");
            return properties.create(null);
        } catch (Exception e) {
            Cobblemontest.LOGGER.error("Failed to create Pokemon from data: " + data.getSpecies(), e);
            return null;
        }
    }

    /**
     * Starts a trainer battle between the player and the trainer's Pokemon.
     * Uses direct battle setup for proper trainer battle mechanics.
     */
    private boolean startTrainerBattle(ServerPlayer player, TrainerEntity trainer, List<Pokemon> trainerPokemonList) {
        try {
            // Get player's party and create battle team
            var playerParty = Cobblemon.INSTANCE.getStorage().getParty(player);
            var playerTeam = playerParty.toBattleTeam(false, false, null);

            // Create the player's battle actor
            PlayerBattleActor playerActor = new PlayerBattleActor(
                    player.getUUID(),
                    playerTeam
            );

            // Create battle Pokemon for all of the trainer's Pokemon with recall operation
            List<BattlePokemon> trainerBattlePokemonList = new ArrayList<>();
            for (Pokemon pokemon : trainerPokemonList) {
                BattlePokemon battlePokemon = new BattlePokemon(
                        pokemon,
                        pokemon,
                        entity -> {
                            // This gets called when the battle ends - recall the Pokemon with animation
                            entity.recallWithAnimation();
                            return kotlin.Unit.INSTANCE;
                        }
                );
                trainerBattlePokemonList.add(battlePokemon);
            }

            // Create the trainer's battle actor with entity backing for animations
            TrainerPokemonBattleActor trainerActor = new TrainerPokemonBattleActor(
                    trainer.getUUID(),
                    trainer.getTrainerName(),
                    trainer,  // Pass the trainer entity
                    trainerBattlePokemonList
            );

            // Note: Pokemon entity will be spawned automatically by Cobblemon with sendout animation

            // Start the battle using BattleRegistry directly
            var result = BattleRegistry.startBattle(
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    new BattleSide(playerActor),
                    new BattleSide(trainerActor),
                    false  // canPreempt
            );

            if (result instanceof SuccessfulBattleStart successResult) {
                PokemonBattle battle = successResult.getBattle();

                // Create and track the battle session
                TrainerBattleSession session = new TrainerBattleSession(
                        battle.getBattleId(),
                        trainer.getUUID(),
                        player.getUUID()
                );
                activeBattles.put(trainer.getUUID(), session);

                // Set trainer as in battle
                trainer.setInBattle(true);

                // Store references for the handler
                final TrainerEntity trainerRef = trainer;
                final ServerPlayer playerRef = player;

                // Register battle end handler
                battle.getOnEndHandlers().add(new TrainerBattleEndHandler(trainerRef, playerRef, this));

                Cobblemontest.LOGGER.info("Trainer battle started between " + player.getName().getString() +
                        " and trainer " + trainer.getTrainerName());

                return true;
            } else {
                // Battle failed to start
                player.sendSystemMessage(Component.literal("Failed to start battle!"));
                return false;
            }
        } catch (Exception e) {
            Cobblemontest.LOGGER.error("Error starting trainer battle", e);
            return false;
        }
    }

    /**
     * Called when a battle ends.
     */
    void onBattleEnd(TrainerEntity trainer, ServerPlayer player) {
        UUID trainerUUID = trainer.getUUID();
        TrainerBattleSession session = activeBattles.remove(trainerUUID);

        if (session != null) {
            trainer.setInBattle(false);

            // Pokemon cleanup is handled automatically by Cobblemon's battle system
            // The recall animation will play automatically when the battle ends

            Cobblemontest.LOGGER.info("Battle ended between " + player.getName().getString() +
                    " and trainer " + trainer.getTrainerName());
        }
    }

    /**
     * Checks if a trainer is currently in a battle.
     */
    public boolean isTrainerInBattle(TrainerEntity trainer) {
        return activeBattles.containsKey(trainer.getUUID());
    }

    /**
     * Gets the trainer ID from the entity for looking up in TrainerData.
     * This extracts the base name used for JSON lookup.
     */
    private String getTrainerIdFromEntity(TrainerEntity trainer) {
        // The trainer name might be different from the JSON file name
        // For now, we'll use a simple lowercase conversion
        // You may want to add a separate trainerId field to TrainerEntity later
        return trainer.getTrainerName().toLowerCase();
    }

    /**
     * Cleans up any battles involving a trainer when they are removed.
     */
    public void onTrainerRemoved(TrainerEntity trainer) {
        // Just remove the battle session - Cobblemon handles entity cleanup
        activeBattles.remove(trainer.getUUID());
    }

    /**
     * Represents an active trainer battle session.
     * Can be expanded to track multiple Pokemon, items used, etc.
     */
    public static class TrainerBattleSession {
        private final UUID battleId;
        private final UUID trainerUUID;
        private final UUID playerUUID;
        private int currentPokemonIndex = 0;

        public TrainerBattleSession(UUID battleId, UUID trainerUUID, UUID playerUUID) {
            this.battleId = battleId;
            this.trainerUUID = trainerUUID;
            this.playerUUID = playerUUID;
        }

        public UUID getBattleId() { return battleId; }
        public UUID getTrainerUUID() { return trainerUUID; }
        public UUID getPlayerUUID() { return playerUUID; }
        public int getCurrentPokemonIndex() { return currentPokemonIndex; }
        public void setCurrentPokemonIndex(int index) { this.currentPokemonIndex = index; }
    }

    /**
     * Handler for battle end events - implements Kotlin Function1 interface
     */
    private static class TrainerBattleEndHandler implements kotlin.jvm.functions.Function1<PokemonBattle, kotlin.Unit> {
        private final TrainerEntity trainer;
        private final ServerPlayer player;
        private final TrainerBattleManager manager;

        public TrainerBattleEndHandler(TrainerEntity trainer, ServerPlayer player, TrainerBattleManager manager) {
            this.trainer = trainer;
            this.player = player;
            this.manager = manager;
        }

        @Override
        public kotlin.Unit invoke(PokemonBattle battle) {
            manager.onBattleEnd(trainer, player);
            return kotlin.Unit.INSTANCE;
        }
    }
}