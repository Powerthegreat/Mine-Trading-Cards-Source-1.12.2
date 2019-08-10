package com.is.mtc.displayer_mono;

import com.is.mtc.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.vector.Vector2f;

import com.is.mtc.MineTradingCards;

public class MonoDisplayerBlockInterface extends GuiContainer {
	private static final int WIDTH = 224, HEIGHT = 90;

	public MonoDisplayerBlockInterface(InventoryPlayer inventoryPlayer, MonoDisplayerBlockTileEntity tileEntity) {
		super(new MonoDisplayerBlockContainer(inventoryPlayer, tileEntity));

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Vector2f drawPos = new Vector2f((width - WIDTH) / 2, (height - HEIGHT) / 2);

		this.drawDefaultBackground();
		mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID, "textures/gui/ui_monodisplayer.png"));
		drawTexturedModalRect((int)drawPos.x, (int)drawPos.y, 0, 0, WIDTH, HEIGHT);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}
}