package org.johnnymod.cobblemontest.client.gui.panel;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Abstract base class for all Trainer Card GUI panels.
 * Each button (Stats, Fight, Gyms, Settings) will have its own panel implementation.
 */
public abstract class TrainerCardPanel {
    
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    
    public TrainerCardPanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Renders the panel content.
     */
    public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
    
    /**
     * Called every tick. Override if panel needs tick-based updates.
     */
    public void tick() {
        // Default: do nothing
    }
    
    /**
     * Called when this panel becomes active.
     */
    public void onActivate() {
        // Default: do nothing
    }
    
    /**
     * Called when this panel becomes inactive.
     */
    public void onDeactivate() {
        // Default: do nothing
    }
}