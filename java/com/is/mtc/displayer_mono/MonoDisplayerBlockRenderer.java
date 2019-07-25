package com.is.mtc.displayer_mono;

import com.is.mtc.Reference;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance;

public class MonoDisplayerBlockRenderer extends TileEntitySpecialRenderer {

	public void render(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		MonoDisplayerBlockTileEntity monoDisplayerBlockTileEntity = (MonoDisplayerBlockTileEntity)tileEntity;

		RenderHelper.disableStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		if (monoDisplayerBlockTileEntity.getWorld().getBlockState(monoDisplayerBlockTileEntity.getPos()).getProperties().get("facing") == EnumFacing.NORTH) {

			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			bufferBuilder.pos(0, 0, 0 - 0.01);
			bufferBuilder.pos(0, 1, 0 - 0.01);
			bufferBuilder.pos(1, 1, 0 - 0.01);
			bufferBuilder.pos(1, 0, 0 - 0.01);
			//tessellator.startDrawingQuads();
			//bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			/*tessellator.addVertexWithUV(0, 0, 0 - 0.01D, 1, 1);
			tessellator.addVertexWithUV(0, 1D, 0 - 0.01D, 1, 0);
			tessellator.addVertexWithUV(1D, 1D, 0 - 0.01D, 0, 0);
			tessellator.addVertexWithUV(1D, 0, 0 - 0.01D, 0, 1);*/
			tessellator.draw();
		} else if (monoDisplayerBlockTileEntity.getWorld().getBlockState(monoDisplayerBlockTileEntity.getPos()).getProperties().get("facing") == EnumFacing.SOUTH) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			bufferBuilder.pos(1, 0, 1.01);
			bufferBuilder.pos(1, 1, 1.01);
			bufferBuilder.pos(0, 1, 1.01);
			bufferBuilder.pos(1, 0, 1.01);
			//tessellator.getBuffer().startDrawingQuads();
			//bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			//tessellator.getBuffer().addVertexData(new int[] {1D, 0, 1.01D, 1, 1});
			//tessellator.getBuffer().addVertexData(new int[] {1D, 1D, 1.01D, 1, 0});
			//tessellator.getBuffer().addVertexData(new int[] {0, 1D, 1.01D, 0, 0});
			//tessellator.getBuffer().addVertexData(new int[] {0, 0, 1.01D, 0, 1});
			tessellator.draw();
		} else if (monoDisplayerBlockTileEntity.getWorld().getBlockState(monoDisplayerBlockTileEntity.getPos()).getProperties().get("facing") == EnumFacing.EAST) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 2);
			bufferBuilder.pos(1.01, 0, 0);
			bufferBuilder.pos(1.01, 1, 0);
			bufferBuilder.pos(1.01, 1, 1);
			bufferBuilder.pos(1.01, 0, 1);
			/*tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			tessellator.getBuffer().addVertexData(new int[] {1.01D, 0, 0, 1, 1});
			tessellator.getBuffer().addVertexData(new int[] {1.01D, 1D, 0, 1, 0});
			tessellator.getBuffer().addVertexData(new int[] {1.01D, 1D, 1D, 0, 0});
			tessellator.getBuffer().addVertexData(new int[] {1.01D, 0, 1D, 0, 1});*/
			tessellator.draw();
		} else if (monoDisplayerBlockTileEntity.getWorld().getBlockState(monoDisplayerBlockTileEntity.getPos()).getProperties().get("facing") == EnumFacing.WEST) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 3);
			bufferBuilder.pos(0 - 0.01, 0, 0);
			bufferBuilder.pos(0 - 0.01, 1, 0);
			bufferBuilder.pos(0 - 0.01, 1, 1);
			bufferBuilder.pos(0 - 0.01, 0, 1);
			/*tessellator.startDrawingQuads();
			bindTextureForSlot(tessellator, monoDisplayerBlockTileEntity, 0);
			tessellator.getBuffer().addVertexData(new int[] {0 - 0.01D, 0, 1D, 1, 1});
			tessellator.getBuffer().addVertexData(new int[] {0 - 0.01D, 1D, 1D, 1, 0});
			tessellator.getBuffer().addVertexData(new int[] {0 - 0.01D, 1D, 0, 0, 0});
			tessellator.getBuffer().addVertexData(new int[] {0 - 0.01D, 0, 0, 0, 1});*/
			tessellator.draw();
		}

		GL11.glPopMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private void bindTextureForSlot(Tessellator tessellator, MonoDisplayerBlockTileEntity dbte, int slot) {
		ItemStack stack;

		if ((stack = dbte.getItemStackInSlot(slot)) != null) {

			if (!Tools.isValidCard(stack))
				tessellator.getBuffer().color(1, 1, 1, 0);
			else {
				CardItem ci = (CardItem)stack.getItem();
				CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

				if (cStruct == null || cStruct.getDynamicTexture() == null) // Card not registered or unregistered illustration, use item image instead
					bindTexture(new ResourceLocation(Reference.MODID, "textures/items/item_card_" + Rarity.toString(ci.getCardRarity()).toLowerCase() + ".png"));
				else {
					cStruct.preloadRessource(instance.renderEngine);
					bindTexture(cStruct.getResourceLocation());
				}

				tessellator.getBuffer().color(1, 1, 1, 1);
			}
		}
		else
			tessellator.getBuffer().color(1, 1, 1, 0);
	}
}