package org.johnnymod.cobblemontest.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.johnnymod.cobblemontest.entity.TrainerEntity;

public class TrainerEntityRenderer extends MobRenderer<TrainerEntity, PlayerModel<TrainerEntity>> {
    
    public TrainerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), true), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TrainerEntity entity) {
        String skinId = entity.getSkinId();
        return ResourceLocation.fromNamespaceAndPath("cobblemontest", "textures/entity/trainer/" + skinId);
    }

    @Override
    public void render(TrainerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
