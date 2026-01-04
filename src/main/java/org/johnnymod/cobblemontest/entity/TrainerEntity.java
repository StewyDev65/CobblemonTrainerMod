package org.johnnymod.cobblemontest.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.johnnymod.cobblemontest.battle.TrainerBattleManager;

public class TrainerEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> TRAINER_NAME =
            SynchedEntityData.defineId(TrainerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SKIN_ID =
            SynchedEntityData.defineId(TrainerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IN_BATTLE =
            SynchedEntityData.defineId(TrainerEntity.class, EntityDataSerializers.BOOLEAN);

    public TrainerEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRAINER_NAME, "Trainer");
        builder.define(SKIN_ID, "trainer1.png");
        builder.define(IN_BATTLE, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Only handle main hand interactions
            if (hand == InteractionHand.MAIN_HAND) {
                TrainerBattleManager.getInstance().startBattle(serverPlayer, this);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("TrainerName", this.getTrainerName());
        nbt.putString("SkinId", this.getSkinId());
        nbt.putBoolean("InBattle", this.isInBattle());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("TrainerName")) {
            this.setTrainerName(nbt.getString("TrainerName"));
        }
        if (nbt.contains("SkinId")) {
            this.setSkinId(nbt.getString("SkinId"));
        }
        if (nbt.contains("InBattle")) {
            this.setInBattle(nbt.getBoolean("InBattle"));
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide) {
            TrainerBattleManager.getInstance().onTrainerRemoved(this);
        }
        super.remove(reason);
    }

    public String getTrainerName() {
        return this.entityData.get(TRAINER_NAME);
    }

    public void setTrainerName(String name) {
        this.entityData.set(TRAINER_NAME, name);
        this.setCustomName(net.minecraft.network.chat.Component.literal(name));
        this.setCustomNameVisible(true);
    }

    public String getSkinId() {
        return this.entityData.get(SKIN_ID);
    }

    public void setSkinId(String skinId) {
        this.entityData.set(SKIN_ID, skinId);
    }

    public boolean isInBattle() {
        return this.entityData.get(IN_BATTLE);
    }

    public void setInBattle(boolean inBattle) {
        this.entityData.set(IN_BATTLE, inBattle);
    }
}