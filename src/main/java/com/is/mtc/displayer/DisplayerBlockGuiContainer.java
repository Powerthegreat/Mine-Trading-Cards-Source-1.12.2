package com.is.mtc.displayer;

import org.lwjgl.util.vector.Vector2f;

import com.is.mtc.util.Reference;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DisplayerBlockGuiContainer extends GuiContainer {
	private InventoryPlayer inventoryPlayer;
	private static final int WIDTH = 224, HEIGHT = 90;

	public DisplayerBlockGuiContainer(InventoryPlayer inventoryPlayer, Container inventorySlots) {
		super(inventorySlots);
		this.inventoryPlayer = inventoryPlayer;

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_displayer.png"));
		Vector2f drawPos = new Vector2f((width - WIDTH) / 2f, (height - HEIGHT) / 2f);

		drawTexturedModalRect((int) drawPos.x, (int) drawPos.y, 0, 0, WIDTH, HEIGHT);
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}

	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		Slot hoveredSlot = getSlotAtPosition(mouseX, mouseY);
		if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack()) {
			ItemStack stack = hoveredSlot.getStack();
			FontRenderer font = stack.getItem().getFontRenderer(stack);
			net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
			drawHoveringText(getItemToolTip(stack), mouseX, mouseY, (font == null ? fontRenderer : font));
			net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
		}
	}

	private Slot getSlotAtPosition(int x, int y) {
		for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = inventorySlots.inventorySlots.get(i);

			if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, y) && slot.isEnabled()) {
				return slot;
			}
		}

		return null;
	}
}
