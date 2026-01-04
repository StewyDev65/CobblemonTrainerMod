package org.johnnymod.cobblemontest.battle;

import com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Battle actor for trainer Pokemon that uses NPC actor type
 * to disable catching and enable surrender options.
 */
public class TrainerPokemonBattleActor extends AIBattleActor {

    private final String trainerName;

    public TrainerPokemonBattleActor(UUID uuid, String trainerName, List<BattlePokemon> pokemonList) {
        super(uuid, pokemonList, new RandomBattleAI());
        this.trainerName = trainerName;
    }

    public TrainerPokemonBattleActor(UUID uuid, String trainerName, List<BattlePokemon> pokemonList, BattleAI ai) {
        super(uuid, pokemonList, ai);
        this.trainerName = trainerName;
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
}