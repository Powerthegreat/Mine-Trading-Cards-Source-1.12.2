package com.is.mtc.displayer_mono;

import com.is.mtc.Reference;
import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance;

public class MonoDisplayerBlockRenderer extends TileEntitySpecialRenderer<MonoDisplayerBlockTileEntity> {
	public void render(MonoDisplayerBlockTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		System.out.println(tileEntity.getWorld().getBlockState(tileEntity.getPos()).getPropertyKeys());//.getProperties().get("facing"));
		if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get("facing") == EnumFacing.NORTH) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
			bindTextureForSlot(tessellator, tileEntity, 0);
			bufferBuilder.pos(0, 0, 0 - 0.01);
			bufferBuilder.pos(0, 1, 0 - 0.01);
			bufferBuilder.pos(1, 1, 0 - 0.01);
			bufferBuilder.pos(1, 0, 0 - 0.01);
			tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get("facing") == EnumFacing.SOUTH) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
			bindTextureForSlot(tessellator, tileEntity, 0);
			bufferBuilder.pos(0, 0, 1.01);
			bufferBuilder.pos(0, 1, 1.01);
			bufferBuilder.pos(1, 1, 1.01);
			bufferBuilder.pos(1, 0, 1.01);
			tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get("facing") == EnumFacing.EAST) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
			bindTextureForSlot(tessellator, tileEntity, 2);
			bufferBuilder.pos(1.01, 0, 0);
			bufferBuilder.pos(1.01, 1, 0);
			bufferBuilder.pos(1.01, 1, 1);
			bufferBuilder.pos(1.01, 0, 1);
			tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get("facing") == EnumFacing.WEST) {
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
			bindTextureForSlot(tessellator, tileEntity, 3);
			bufferBuilder.pos(0 - 0.01, 0, 0);
			bufferBuilder.pos(0 - 0.01, 1, 0);
			bufferBuilder.pos(0 - 0.01, 1, 1);
			bufferBuilder.pos(0 - 0.01, 0, 1);
			tessellator.draw();
		}

		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private void bindTextureForSlot(Tessellator tessellator, MonoDisplayerBlockTileEntity monoDisplayerBlockTileEntity, int slot) {
		ItemStack stack = monoDisplayerBlockTileEntity.getStackInSlot(slot);

		if (!(stack.isEmpty())) {
			if (!Tools.isValidCard(stack))
				tessellator.getBuffer().color(1, 1, 1, 0);
			else {
				CardItem cardItem = (CardItem) stack.getItem();
				CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

				if (cStruct == null || cStruct.getDynamicTexture() == null) // Card not registered or unregistered illustration, use item image instead
					bindTexture(new ResourceLocation(Reference.MODID, "textures/items/item_card_" + Rarity.toString(cardItem.getCardRarity()).toLowerCase() + ".png"));
				else {
					cStruct.preloadResource(instance.renderEngine);
					bindTexture(cStruct.getResourceLocation());
				}

				tessellator.getBuffer().color(1, 1, 1, 1);
			}
		} else
			tessellator.getBuffer().color(1, 1, 1, 0);
	}
}