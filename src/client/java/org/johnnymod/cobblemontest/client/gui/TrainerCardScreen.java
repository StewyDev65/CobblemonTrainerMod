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
    
    // Button textures
    private static final ResourceLocation BUTTON_STATS =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_stats.png");
    private static final ResourceLocation BUTTON_FIGHT =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_fight.png");
    private static final ResourceLocation BUTTON_GYMS =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_gyms.png");
    private static final ResourceLocation BUTTON_SETTINGS =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_settings.png");

    // Button textures
    private static final ResourceLocation BUTTON_STATS_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_stats_hover.png");
    private static final ResourceLocation BUTTON_FIGHT_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_fight_hover.png");
    private static final ResourceLocation BUTTON_GYMS_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_gyms_hover.png");
    private static final ResourceLocation BUTTON_SETTINGS_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_settings_hover.png");

    // Active button textures
    private static final ResourceLocation BUTTON_STATS_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_stats_active.png");
    private static final ResourceLocation BUTTON_FIGHT_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_fight_active.png");
    private static final ResourceLocation BUTTON_GYMS_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_gyms_active.png");
    private static final ResourceLocation BUTTON_SETTINGS_ACTIVE =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_settings_active.png");

    // Active hover button textures
    private static final ResourceLocation BUTTON_STATS_ACTIVE_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_stats_active_hover.png");
    private static final ResourceLocation BUTTON_FIGHT_ACTIVE_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_fight_active_hover.png");
    private static final ResourceLocation BUTTON_GYMS_ACTIVE_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_gyms_active_hover.png");
    private static final ResourceLocation BUTTON_SETTINGS_ACTIVE_HOVER =
            ResourceLocation.fromNamespaceAndPath(Cobblemontest.MOD_ID, "textures/gui/button_settings_active_hover.png");


    private static final int TEXTURE_WIDTH = 225;
    private static final int TEXTURE_HEIGHT = 134;
    
    // Button texture dimensions
    private static final int BUTTON_TEXTURE_WIDTH = 16;
    private static final int BUTTON_TEXTURE_HEIGHT = 16;
    
    // Button scale
    private static final float BUTTON_SCALE = 1.0f / 1.2f;

    private int leftPos;
    private int topPos;
    
    // Store button positions for text rendering
    private int buttonX;
    private int buttonStartY;
    private int buttonSpacing;

    private CustomTextureButton currentActiveButton;

    public TrainerCardScreen() {
        super(Component.literal("Trainer Card"));
    }

    @Override
    protected void init() {
        super.init();

        // Center the GUI on screen
        this.leftPos = (this.width - TEXTURE_WIDTH) / 2;
        this.topPos = (this.height - TEXTURE_HEIGHT) / 2;

        // Button positioning
        this.buttonX = leftPos + 10;
        this.buttonStartY = topPos + 26;
        this.buttonSpacing = 23;
        
        // Calculate clickable area dimensions
        int buttonVisualSize = (int)(16 * BUTTON_SCALE);  // ~13 pixels visual size
        
        // Button 1 - Stats (clickable area extends to cover "Stats" text)
        CustomTextureButton statsButton = new CustomTextureButton(
            buttonX,
            buttonStartY,
            16,
            16,
            40,
            buttonVisualSize,
            BUTTON_STATS,
            BUTTON_STATS_HOVER,  // Using same for hover (change if needed)
            BUTTON_STATS_ACTIVE,        // NEW
            BUTTON_STATS_ACTIVE_HOVER,  // NEW
            BUTTON_TEXTURE_WIDTH,
            BUTTON_TEXTURE_HEIGHT,
            BUTTON_SCALE,
            button -> {
                setActiveButton((CustomTextureButton) button);
                Cobblemontest.LOGGER.info("Stats button clicked!");
            }
        );
        this.addRenderableWidget(statsButton);
        
        // Button 2 - Fight
        CustomTextureButton fightButton = new CustomTextureButton(
            buttonX,
            buttonStartY + buttonSpacing,
            16,
            16,
            40,
            buttonVisualSize,
            BUTTON_FIGHT,
            BUTTON_FIGHT_HOVER,
            BUTTON_FIGHT_ACTIVE,        // NEW
            BUTTON_FIGHT_ACTIVE_HOVER,  // NEW
            BUTTON_TEXTURE_WIDTH,
            BUTTON_TEXTURE_HEIGHT,
            BUTTON_SCALE,
            button -> {
                setActiveButton((CustomTextureButton) button);
                Cobblemontest.LOGGER.info("Fight button clicked!");
            }
        );
        this.addRenderableWidget(fightButton);
        
        // Button 3 - Gyms
        CustomTextureButton gymsButton = new CustomTextureButton(
            buttonX,
            buttonStartY + buttonSpacing * 2,
            16,
            16,
            40,
            buttonVisualSize,
            BUTTON_GYMS,
            BUTTON_GYMS_HOVER,
            BUTTON_GYMS_ACTIVE,         // NEW
            BUTTON_GYMS_ACTIVE_HOVER,   // NEW
            BUTTON_TEXTURE_WIDTH,
            BUTTON_TEXTURE_HEIGHT,
            BUTTON_SCALE,
            button -> {
                setActiveButton((CustomTextureButton) button);
                Cobblemontest.LOGGER.info("Gyms button clicked!");
            }
        );
        this.addRenderableWidget(gymsButton);
        
        // Button 4 - Settings (wider clickable area for longer text)
        CustomTextureButton settingsButton = new CustomTextureButton(
            buttonX,
            buttonStartY + buttonSpacing * 3,
            16,
            16,
            55,
            buttonVisualSize,
            BUTTON_SETTINGS,
            BUTTON_SETTINGS_HOVER,
            BUTTON_SETTINGS_ACTIVE,        // NEW
            BUTTON_SETTINGS_ACTIVE_HOVER,  // NEW
            BUTTON_TEXTURE_WIDTH,
            BUTTON_TEXTURE_HEIGHT,
            BUTTON_SCALE,
            button -> {
                setActiveButton((CustomTextureButton) button);
                Cobblemontest.LOGGER.info("Settings button clicked!");
            }
        );
        this.addRenderableWidget(settingsButton);

        // Set Stats button as active by default
        setActiveButton(statsButton);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark overlay
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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render button labels
        renderButtonLabels(guiGraphics);
    }
    
    private void renderButtonLabels(GuiGraphics guiGraphics) {
        // Calculate text position (to the right of buttons)
        int textX = buttonX + (int)(16 * BUTTON_SCALE) + 4;
        
        // Text color (white)
        int textColor = 0x001A00;
        
        // Vertical offset for text alignment
        int textYOffset = 3;
        
        // Render each label
        guiGraphics.drawString(
            this.font,
            "Stats",
            textX,
            buttonStartY + textYOffset,
            textColor,
            false
        );
        
        guiGraphics.drawString(
            this.font,
            "Fight",
            textX,
            buttonStartY + buttonSpacing + textYOffset,
            textColor,
            false
        );
        
        guiGraphics.drawString(
            this.font,
            "Gyms",
            textX,
            buttonStartY + buttonSpacing * 2 + textYOffset,
            textColor,
            false
        );
        
        guiGraphics.drawString(
            this.font,
            "Settings",
            textX,
            buttonStartY + buttonSpacing * 3 + textYOffset,
            textColor,
            false
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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

    private void setActiveButton(CustomTextureButton newActiveButton) {
        // Deactivate the current active button
        if (currentActiveButton != null) {
            currentActiveButton.setActive(false);
        }
        
        // Set the new active button
        currentActiveButton = newActiveButton;
        currentActiveButton.setActive(true);
    }
}