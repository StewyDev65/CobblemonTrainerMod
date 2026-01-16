package org.johnnymod.cobblemontest.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.johnnymod.cobblemontest.Cobblemontest;

public class ModItems {
    
    public static final Item TRAINER_CARD = register("trainer_card", 
            new TrainerCardItem(new Item.Properties().stacksTo(1)));
    
    private static Item register(String name, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, name),
                item
        );
    }
    
    public static void initialize() {
        Cobblemontest.LOGGER.info("Registering Trainer Card item");
    }
}