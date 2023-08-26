package com.is.mtc.displayer_mono;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance;

import org.lwjgl.opengl.GL11;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class MonoDisplayerBlockRenderer extends TileEntitySpecialRenderer<MonoDisplayerBlockTileEntity> {
	public void render(MonoDisplayerBlockTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		boolean displayTexture = bindTextureForSlot(tessellator, tileEntity, 0);
		if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.NORTH && displayTexture) {
			bufferBuilder.normal(0, 0, -1);
			bufferBuilder.pos(0, 0, 0 - 0.01).tex(1, 1).endVertex();
			bufferBuilder.pos(0, 1, 0 - 0.01).tex(1, 0).endVertex();
			bufferBuilder.pos(1, 1, 0 - 0.01).tex(0, 0).endVertex();
			bufferBuilder.pos(1, 0, 0 - 0.01).tex(0, 1).endVertex();
			//tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.SOUTH && displayTexture) {
			//bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferBuilder.normal(0, 0, 1);
			bufferBuilder.pos(0, 0, 1.01).tex(0, 1).endVertex();
			bufferBuilder.pos(1, 0, 1.01).tex(1, 1).endVertex();
			bufferBuilder.pos(1, 1, 1.01).tex(1, 0).endVertex();
			bufferBuilder.pos(0, 1, 1.01).tex(0, 0).endVertex();
			//tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.EAST && displayTexture) {
			//bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferBuilder.normal(1, 0, 0);
			bufferBuilder.pos(1.01, 0, 0).tex(1, 1).endVertex();
			bufferBuilder.pos(1.01, 1, 0).tex(1, 0).endVertex();
			bufferBuilder.pos(1.01, 1, 1).tex(0, 0).endVertex();
			bufferBuilder.pos(1.01, 0, 1).tex(0, 1).endVertex();
			//tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.WEST && displayTexture) {
			//bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferBuilder.normal(-1, 0, 0);
			bufferBuilder.pos(0 - 0.01, 0, 0).tex(0, 1).endVertex();
			bufferBuilder.pos(0 - 0.01, 0, 1).tex(1, 1).endVertex();
			bufferBuilder.pos(0 - 0.01, 1, 1).tex(1, 0).endVertex();
			bufferBuilder.pos(0 - 0.01, 1, 0).tex(0, 0).endVertex();
		}

		tessellator.draw();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private boolean bindTextureForSlot(Tessellator tessellator, MonoDisplayerBlockTileEntity monoDisplayerBlockTileEntity, int slot) {
		ItemStack stack = monoDisplayerBlockTileEntity.getStackInSlot(slot);

		if (Tools.isValidCard(stack)) {
			CardItem cardItem = (CardItem) stack.getItem();
			CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

			if (cStruct == null || cStruct.getAssetLocation() == null) // Card not registered or unregistered illustration, use item image instead
				bindTexture(new ResourceLocation(Reference.MODID, "textures/items/item_card_" + Rarity.toString(cardItem.getCardRarity()).toLowerCase() + ".png"));
			else {
				bindTexture(cStruct.getAssetLocation());
			}

			tessellator.getBuffer().color(1f, 1f, 1f, 1f);
			return true;
		} else {
			tessellator.getBuffer().color(1f, 1f, 1f, 0f);
			return false;
		}
	}
}