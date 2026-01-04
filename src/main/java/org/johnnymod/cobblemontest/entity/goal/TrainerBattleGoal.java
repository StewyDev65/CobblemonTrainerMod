package org.johnnymod.cobblemontest.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.johnnymod.cobblemontest.entity.TrainerEntity;

import java.util.EnumSet;

/**
 * AI Goal that activates when trainer is in battle.
 * Stops all movement and makes trainer look at the player they're battling.
 */
public class TrainerBattleGoal extends Goal {

    private final TrainerEntity trainer;
    private Player targetPlayer;

    public TrainerBattleGoal(TrainerEntity trainer) {
        this.trainer = trainer;
        // Block movement and looking - this goal takes full control
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return trainer.isInBattle();
    }

    @Override
    public boolean canContinueToUse() {
        return trainer.isInBattle();
    }

    @Override
    public void start() {
        // Stop all navigation
        trainer.getNavigation().stop();

        // Find the player we're battling (nearest player for now)
        targetPlayer = trainer.level().getNearestPlayer(trainer, 16.0);
    }

    @Override
    public void stop() {
        targetPlayer = null;
    }

    @Override
    public void tick() {
        // Keep navigation stopped
        trainer.getNavigation().stop();

        // Look at the player we're battling
        if (targetPlayer != null && targetPlayer.isAlive()) {
            trainer.getLookControl().setLookAt(
                    targetPlayer.getX(),
                    targetPlayer.getEyeY(),
                    targetPlayer.getZ()
            );
        }
    }
}