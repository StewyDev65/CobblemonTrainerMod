package org.johnnymod.cobblemontest.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.johnnymod.cobblemontest.Cobblemontest;
import org.lwjgl.glfw.GLFW;

public class TrainerCardScreen extends Screen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/trainer_menu.png");

    // Change these to match your actual texture dimensions
    private static final int TEXTURE_WIDTH = 225;   // Change to your texture width
    private static final int TEXTURE_HEIGHT = 134;  // Change to your texture height

    private int leftPos;
    private int topPos;

    public TrainerCardScreen() {
        super(Component.literal("Trainer Card"));
    }

    @Override
    protected void init() {
        super.init();

        // Center the GUI on screen
        this.leftPos = (this.width - TEXTURE_WIDTH) / 2;
        this.topPos = (this.height - TEXTURE_HEIGHT) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark background overlay
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Ensure proper shader and texture are bound and no tinting is applied
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);

    // Draw the GUI texture - MUST use 8-parameter version for non-256x256 textures
    guiGraphics.blit(
        TEXTURE,
        leftPos,                // x position on screen
        topPos,                 // y position on screen
        0,                      // u - texture X coordinate
        0,                      // v - texture Y coordinate
        TEXTURE_WIDTH,          // width to render on screen
        TEXTURE_HEIGHT,         // height to render on screen
        TEXTURE_WIDTH,          // actual texture file width (for UV calculation)
        TEXTURE_HEIGHT          // actual texture file height (for UV calculation)
    );

        // Render children / tooltips - disabled for now as it was causing blur
        // super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close with ESC or inventory key (default 'E')
        if (keyCode == GLFW.GLFW_KEY_ESCAPE ||
                this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        // Don't pause the game when this screen is open
        return false;
    }
}