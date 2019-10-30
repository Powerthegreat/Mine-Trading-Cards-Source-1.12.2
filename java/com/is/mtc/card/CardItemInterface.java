package com.is.mtc.card;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class CardItemInterface extends GuiScreen {
	private static final double UI_WIDTH = 224, UI_HEIGHT = 224;

	private CardStructure cStruct;
	private ItemStack stack;

	public CardItemInterface(ItemStack stack) {
		this.stack = stack;
		cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		double dpx = (width - CardItemInterface.UI_WIDTH) / 2, dpy = (height - CardItemInterface.UI_HEIGHT) / 2;

		cStruct.preloadResource(mc.getTextureManager(), stack.getTagCompound().getInteger("assetnumber"));
		//System.out.println(cStruct.getResourceLocation());
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(cStruct.getResourceLocation());
		//mc.getTextureManager().getTexture(cStruct.getResourceLocation());

		drawTexturedModalRect(dpx, dpy, CardItemInterface.UI_WIDTH, CardItemInterface.UI_HEIGHT);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	// Adapted drawing
	public void drawTexturedModalRect(double x, double y, double width, double height) { // Custom for 01 size
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
		tessellator.getBuffer().pos(x + 0, y + height, (int) this.zLevel).tex(0, 1).endVertex();
		tessellator.getBuffer().pos(x + width, y + height, (int) this.zLevel).tex(1, 1).endVertex();
		tessellator.getBuffer().pos(x + width, y + 0, (int) this.zLevel).tex(1, 0).endVertex();
		tessellator.getBuffer().pos(x + 0, y + 0, (int) this.zLevel).tex(0, 0).endVertex();
		tessellator.draw();
		/*.tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV())*/
		//Tessellator tessellator = Tessellator.getInstance();
		////tessellator.startDrawingQuads();
		//tessellator.getBuffer().addVertexData(new int[] {x + 0, y + height, (int) this.zLevel, 0, 1});
		//tessellator.getBuffer().addVertexData(new int[] {x + width, y + height, (int) this.zLevel, 1, 1});
		//tessellator.getBuffer().addVertexData(new int[] {x + width, y + 0, (int) this.zLevel, 1, 0});
		//tessellator.getBuffer().addVertexData(new int[] {x + 0, y + 0, (int) this.zLevel, 0, 0});
		//tessellator.draw();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
