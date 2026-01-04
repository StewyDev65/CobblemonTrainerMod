package org.johnnymod.cobblemontest.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    
    public static final EntityType<TrainerEntity> TRAINER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("cobblemontest", "trainer"),
            EntityType.Builder.of(TrainerEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build(String.valueOf(ResourceLocation.fromNamespaceAndPath("cobblemontest", "trainer")))
    );

    public static void initialize() {
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(TRAINER, TrainerEntity.createAttributes());
    }
}
