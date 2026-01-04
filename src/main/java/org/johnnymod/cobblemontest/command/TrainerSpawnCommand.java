package org.johnnymod.cobblemontest.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.johnnymod.cobblemontest.data.TrainerData;
import org.johnnymod.cobblemontest.entity.ModEntities;
import org.johnnymod.cobblemontest.entity.TrainerEntity;

public class TrainerSpawnCommand {
    
    private static final SuggestionProvider<CommandSourceStack> TRAINER_SUGGESTIONS = 
            (context, builder) -> SharedSuggestionProvider.suggest(
                    TrainerData.getAllTrainers().keySet(),
                    builder
            );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("trainerspawn")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("trainer_name", StringArgumentType.string())
                                .suggests(TRAINER_SUGGESTIONS)
                                .executes(context -> {
                                    String trainerName = StringArgumentType.getString(context, "trainer_name");
                                    return spawnTrainer(context.getSource(), trainerName);
                                })
                        )
        );
    }

    private static int spawnTrainer(CommandSourceStack source, String trainerName) {
        TrainerData.TrainerDefinition definition = TrainerData.getTrainer(trainerName);
        
        if (definition == null) {
            source.sendFailure(Component.literal("Trainer '" + trainerName + "' not found!"));
            return 0;
        }

        ServerLevel world = source.getLevel();
        Vec3 position = source.getPosition();

        TrainerEntity trainer = ModEntities.TRAINER.create(world);
        if (trainer != null) {
            trainer.setPos(position.x, position.y, position.z);
            trainer.setTrainerName(definition.getDisplayName());
            trainer.setSkinId(definition.getSkinId());
            
            world.addFreshEntity(trainer);
            
            source.sendSuccess(
                    () -> Component.literal("Spawned trainer: " + definition.getDisplayName()),
                    true
            );
            return 1;
        }

        source.sendFailure(Component.literal("Failed to create trainer entity!"));
        return 0;
    }
}
