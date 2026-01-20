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
    private final ResourceLocation activeTexture;      // NEW
    private final ResourceLocation activeHoverTexture; // NEW
    private final int textureWidth;
    private final int textureHeight;
    private final float scale;
    private final int originalWidth;
    private final int originalHeight;
    private boolean active = false;  // NEW

    public CustomTextureButton(
            int x, 
            int y, 
            int width, 
            int height,
            int clickableWidth,
            int clickableHeight,
            ResourceLocation normalTexture,
            ResourceLocation hoverTexture,
            ResourceLocation activeTexture,      // NEW PARAMETER
            ResourceLocation activeHoverTexture, // NEW PARAMETER
            int textureWidth,
            int textureHeight,
            float scale,
            OnPress onPress) {
        super(x, y, clickableWidth, clickableHeight, Component.empty(), onPress, DEFAULT_NARRATION);
        this.normalTexture = normalTexture;
        this.hoverTexture = hoverTexture;
        this.activeTexture = activeTexture;           // NEW
        this.activeHoverTexture = activeHoverTexture; // NEW
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
        this.originalWidth = width;
        this.originalHeight = height;
    }
    
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Determine which texture to use based on hover state
        ResourceLocation texture;
        if (active) {
            texture = this.isHovered ? activeHoverTexture : activeTexture;
        } else {
            texture = this.isHovered ? hoverTexture : normalTexture;
        }
        
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
        
        // Render the button texture using ORIGINAL dimensions (visual size stays the same)
        guiGraphics.blit(
            texture,
            0,
            0,
            0,
            0,
            originalWidth,   // Use original width for visual
            originalHeight,  // Use original height for visual
            textureWidth,
            textureHeight
        );
        
        guiGraphics.pose().popPose();
        
        // Disable blending after rendering
        RenderSystem.disableBlend();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }
}