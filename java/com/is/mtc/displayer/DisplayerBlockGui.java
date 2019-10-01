package com.is.mtc.displayer;

import com.is.mtc.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class DisplayerBlockGui extends GuiContainer {

	private InventoryPlayer inventoryPlayer;
	private static final int WIDTH = 224, HEIGHT = 90;

	public DisplayerBlockGui(InventoryPlayer inventoryPlayer, DisplayerBlockContainer container) {
		super(container);
		this.inventoryPlayer = inventoryPlayer;

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_displayer.png"));
		Vector2f drawPos = new Vector2f((width - WIDTH) / 2f, (height - HEIGHT) / 2f);

		//this.drawDefaultBackground();
		//mc.renderEngine.bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_displayer.png"));
		//mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_displayer.png"));
		drawTexturedModalRect((int) drawPos.x, (int) drawPos.y, 0, 0, WIDTH, HEIGHT);
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}
}
