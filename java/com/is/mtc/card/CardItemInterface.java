package com.is.mtc.card;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;

public class CardItemInterface extends GuiScreen {
	private static final int UI_WIDTH = 224, UI_HEIGHT = 224;

	private CardStructure cStruct;

	public CardItemInterface(ItemStack stack) {
		cStruct = Databank.getCardByCDWD(stack.stackTagCompound.getString("cdwd"));
	}

	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		int dpx = (width - CardItemInterface.UI_WIDTH) / 2, dpy = (height - CardItemInterface.UI_HEIGHT) / 2;

		cStruct.preloadRessource(mc.getTextureManager());
		drawDefaultBackground();
		mc.renderEngine.bindTexture(cStruct.getResourceLocation());
		drawTexturedModalRect(dpx, dpy, CardItemInterface.UI_WIDTH, CardItemInterface.UI_HEIGHT);
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}

	// Adapted drawing
	public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_5_, int p_73729_6_) { // Custom for 01 size
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(p_73729_1_ + 0, p_73729_2_ + p_73729_6_, this.zLevel, 0, 1);
		tessellator.addVertexWithUV(p_73729_1_ + p_73729_5_, p_73729_2_ + p_73729_6_, this.zLevel, 1, 1);
		tessellator.addVertexWithUV(p_73729_1_ + p_73729_5_, p_73729_2_ + 0, this.zLevel, 1, 0);
		tessellator.addVertexWithUV(p_73729_1_ + 0, p_73729_2_ + 0, this.zLevel, 0, 0);
		tessellator.draw();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
