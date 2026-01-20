package org.johnnymod.cobblemontest.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomTextureButton extends Button {
    
    private final ResourceLocation normalTexture;
    private final ResourceLocation hoverTexture;
    private final int textureWidth;
    private final int textureHeight;
    private final float scale;
    private final int originalWidth;  // Store the original unscaled width
    private final int originalHeight; // Store the original unscaled height
    
    public CustomTextureButton(
            int x, 
            int y, 
            int width, 
            int height,
            ResourceLocation normalTexture,
            ResourceLocation hoverTexture,
            int textureWidth,
            int textureHeight,
            float scale,
            OnPress onPress) {
        // The actual clickable area size (scaled)
        super(x, y, (int)(width * scale), (int)(height * scale), Component.empty(), onPress, DEFAULT_NARRATION);
        this.normalTexture = normalTexture;
        this.hoverTexture = hoverTexture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
        this.originalWidth = width;   // Store original width
        this.originalHeight = height; // Store original height
    }
    
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Determine which texture to use based on hover state
        ResourceLocation texture = this.isHovered ? hoverTexture : normalTexture;
        
        // Enable blending for transparency support
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // Set up rendering state for crisp texture
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        
        // Apply scaling
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        
        // Render the button texture using ORIGINAL dimensions (no rounding errors)
        guiGraphics.blit(
            texture,
            0,
            0,
            0,
            0,
            originalWidth,   // Use original width
            originalHeight,  // Use original height
            textureWidth,
            textureHeight
        );
        
        guiGraphics.pose().popPose();
        
        // Disable blending after rendering
        RenderSystem.disableBlend();
    }
}