package com.is.mtc.displayer_mono;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.MineTradingCards;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;

public class MonoDisplayerBlockRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par5) {
		Tessellator tessellator = Tessellator.instance;
		MonoDisplayerBlockTileEntity dbte = (MonoDisplayerBlockTileEntity)te;

		RenderHelper.disableStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		if (dbte.getWorldObj().getBlockMetadata(dbte.xCoord, dbte.yCoord, dbte.zCoord) == Tools.SIDE_NORTH)
		{
			tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, dbte, 0);
			tessellator.addVertexWithUV(0, 0, 0 - 0.01D, 1, 1);
			tessellator.addVertexWithUV(0, 1D, 0 - 0.01D, 1, 0);
			tessellator.addVertexWithUV(1D, 1D, 0 - 0.01D, 0, 0);
			tessellator.addVertexWithUV(1D, 0, 0 - 0.01D, 0, 1);
			tessellator.draw();
		}


		else if (dbte.getWorldObj().getBlockMetadata(dbte.xCoord, dbte.yCoord, dbte.zCoord) == Tools.SIDE_SOUTH)
		{
			tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, dbte, 0);
			tessellator.addVertexWithUV(1D, 0, 1.01D, 1, 1);
			tessellator.addVertexWithUV(1D, 1D, 1.01D, 1, 0);
			tessellator.addVertexWithUV(0, 1D, 1.01D, 0, 0);
			tessellator.addVertexWithUV(0, 0, 1.01D, 0, 1);
			tessellator.draw();
		}

		else if (dbte.getWorldObj().getBlockMetadata(dbte.xCoord, dbte.yCoord, dbte.zCoord) == Tools.SIDE_EAST)
		{
			tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, dbte, 0);
			tessellator.addVertexWithUV(1.01D, 0, 0, 1, 1);
			tessellator.addVertexWithUV(1.01D, 1D, 0, 1, 0);
			tessellator.addVertexWithUV(1.01D, 1D, 1D, 0, 0);
			tessellator.addVertexWithUV(1.01D, 0, 1D, 0, 1);
			tessellator.draw();
		}

		else if (dbte.getWorldObj().getBlockMetadata(dbte.xCoord, dbte.yCoord, dbte.zCoord) == Tools.SIDE_WEST)
		{
			tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, dbte, 0);
			tessellator.addVertexWithUV(0 - 0.01D, 0, 1D, 1, 1);
			tessellator.addVertexWithUV(0 - 0.01D, 1D, 1D, 1, 0);
			tessellator.addVertexWithUV(0 - 0.01D, 1D, 0, 0, 0);
			tessellator.addVertexWithUV(0 - 0.01D, 0, 0, 0, 1);
			tessellator.draw();
		}

		GL11.glPopMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private void bindTextureForSlot(Tessellator tessellator, MonoDisplayerBlockTileEntity dbte, int slot) {
		ItemStack stack;

		if ((stack = dbte.getItemStackInSlot(slot)) != null) {

			if (!Tools.isValidCard(stack))
				tessellator.setColorRGBA_F(1, 1, 1, 0);
			else {
				CardItem ci = (CardItem)stack.getItem();
				CardStructure cStruct = Databank.getCardByCDWD(stack.stackTagCompound.getString("cdwd"));

				if (cStruct == null || cStruct.getDynamicTexture() == null) // Card not registered or unregistered illustration, use item image instead
					bindTexture(new ResourceLocation(MineTradingCards.MODID, "textures/items/item_card_" + Rarity.toString(ci.getCardRarity()).toLowerCase() + ".png"));
				else {
					cStruct.preloadRessource(field_147501_a.field_147553_e);
					bindTexture(cStruct.getResourceLocation());
				}

				tessellator.setColorRGBA_F(1, 1, 1, 1);
			}
		}
		else
			tessellator.setColorRGBA_F(1, 1, 1, 0);
	}
}