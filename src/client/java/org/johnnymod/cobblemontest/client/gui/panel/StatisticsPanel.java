package org.johnnymod.cobblemontest.client.gui.panel;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Statistics panel for the Trainer Card GUI.
 * Displays player name and skin head preview.
 */
public class StatisticsPanel extends TrainerCardPanel {
    
    private final Minecraft minecraft;
    
    public StatisticsPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.minecraft = Minecraft.getInstance();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft.player == null) return;
        
        // Head size and positioning
        int headSize = 16;
        int padding = 8;
        int gap = 6; // Gap between head and name
        
        // Calculate positions from right edge: [HEAD] [NAME]
        String playerName = minecraft.player.getName().getString();
        int nameWidth = minecraft.font.width(playerName);
        
        int rightEdge = x + width - padding;
        int nameX = rightEdge - nameWidth;
        int headX = nameX - gap - headSize;
        int headY = y + padding;
        
        // Center name vertically with head
        int nameY = headY + (headSize - minecraft.font.lineHeight) / 2;
        
        // Render player head
        renderPlayerHead(guiGraphics, headX, headY, headSize);
        
        // Render player name to the right of head
        guiGraphics.drawString(minecraft.font, playerName, nameX, nameY, 0x001A00, false);
    }
    
    /**
     * Renders the player's head from their skin texture.
     * Includes both the base face layer and the overlay (hat) layer.
     */
    private void renderPlayerHead(GuiGraphics guiGraphics, int x, int y, int size) {
        if (minecraft.player == null) return;
        
        PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
        if (playerInfo == null) return;
        
        ResourceLocation skinTexture = playerInfo.getSkin().texture();
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, skinTexture);
        
        // Render base layer (face) - located at (8, 8) in the 64x64 skin texture
        guiGraphics.blit(
            skinTexture,
            x, y,              // Screen position
            size, size,        // Size on screen
            8.0F, 8.0F,        // Texture UV start (face location)
            8, 8,              // Texture region size (8x8 pixels)
            64, 64             // Full texture dimensions
        );
        
        // Render overlay layer (hat/accessories) - located at (40, 8)
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        guiGraphics.blit(
            skinTexture,
            x, y,              // Screen position (same as base)
            size, size,        // Size on screen
            40.0F, 8.0F,       // Texture UV start (overlay location)
            8, 8,              // Texture region size
            64, 64             // Full texture dimensions
        );
        
        RenderSystem.disableBlend();
    }
}