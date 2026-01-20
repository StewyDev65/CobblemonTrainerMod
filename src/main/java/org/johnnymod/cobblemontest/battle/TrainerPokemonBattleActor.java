package org.johnnymod.cobblemontest.battle;

import com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor;
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.johnnymod.cobblemontest.entity.TrainerEntity;

import java.util.List;
import java.util.UUID;

/**
 * Battle actor for trainer Pokemon that implements EntityBackedBattleActor
 * to enable proper sendout and recall animations from the trainer entity.
 */
public class TrainerPokemonBattleActor extends AIBattleActor implements EntityBackedBattleActor<TrainerEntity> {

    private final TrainerEntity trainerEntity;
    private final String trainerName;
    private final Vec3 initialPos;

    public TrainerPokemonBattleActor(UUID uuid, String trainerName, TrainerEntity trainerEntity,
                                     List<BattlePokemon> pokemonList) {
        super(uuid, pokemonList, new RandomBattleAI());
        this.trainerName = trainerName;
        this.trainerEntity = trainerEntity;
        this.initialPos = trainerEntity.position();
    }

    public TrainerPokemonBattleActor(UUID uuid, String trainerName, TrainerEntity trainerEntity,
                                     List<BattlePokemon> pokemonList, BattleAI ai) {
        super(uuid, pokemonList, ai);
        this.trainerName = trainerName;
        this.trainerEntity = trainerEntity;
        this.initialPos = trainerEntity.position();
    }

    @NotNull
    @Override
    public ActorType getType() {
        return ActorType.NPC;
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        return Component.literal(trainerName);
    }

    @NotNull
    @Override
    public MutableComponent nameOwned(@NotNull String pokemon) {
        return Component.literal(trainerName + "'s " + pokemon);
    }

    @Nullable
    @Override
    public TrainerEntity getEntity() {
        // Return the trainer entity if it's still alive and in the world
        return (trainerEntity.isAlive() && !trainerEntity.isRemoved()) ? trainerEntity : null;
    }

    @Nullable
    @Override
    public Vec3 getInitialPos() {
        return initialPos;
    }
}