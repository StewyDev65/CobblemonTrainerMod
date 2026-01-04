package org.johnnymod.cobblemontest.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.johnnymod.cobblemontest.client.renderer.TrainerEntityRenderer;
import org.johnnymod.cobblemontest.entity.ModEntities;

public class ModEntityRenderers {
    
    public static void initialize() {
        EntityRendererRegistry.register(ModEntities.TRAINER, TrainerEntityRenderer::new);
    }
}
