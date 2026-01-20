package org.johnnymod.cobblemontest.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
    private static final int TEXTURE_WIDTH = 225;
    private static final int TEXTURE_HEIGHT = 134;

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

        // Add a test button
        Button testButton = Button.builder(
            Component.literal("Test"),
            button -> {
                Cobblemontest.LOGGER.info("Button clicked!");
            }
        )
        .bounds(
            leftPos + 10,   // x position
            topPos + 20,    // y position
            60,             // width
            20              // height
        )
        .build();

        this.addRenderableWidget(testButton);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Override to provide custom background rendering without blur
        // Render dark overlay (optional - remove if you don't want it)
        this.renderTransparentBackground(guiGraphics);
        
        // Render our custom GUI texture
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(
            TEXTURE,
            leftPos,
            topPos,
            0,
            0,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Now we can safely call super.render() - it will use our custom renderBackground()
        super.render(guiGraphics, mouseX, mouseY, partialTick);
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
        return false;
    }
}