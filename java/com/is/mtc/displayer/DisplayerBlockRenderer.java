package com.is.mtc.displayer;

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
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance;

public class DisplayerBlockRenderer extends TileEntitySpecialRenderer<DisplayerBlockTileEntity> {
	public void render(DisplayerBlockTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		/*Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();

		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);*/

		// Facing north face
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bindTextureForSlot(tessellator, tileEntity, 0);
		bufferBuilder.pos(0, 0, 0 - 0.01).endVertex();
		bufferBuilder.pos(0, 1, 0 - 0.01).endVertex();
		bufferBuilder.pos(1, 1, 0 - 0.01).endVertex();
		bufferBuilder.pos(1, 0, 0 - 0.01).endVertex();
		tessellator.draw();

		// Facing south face
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bindTextureForSlot(tessellator, tileEntity, 1);
		bufferBuilder.pos(0, 0, 1.01).endVertex();
		bufferBuilder.pos(1, 0, 1.01).endVertex();
		bufferBuilder.pos(1, 1, 1.01).endVertex();
		bufferBuilder.pos(0, 1, 1.01).endVertex();
		tessellator.draw();

		// Facing east face
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bindTextureForSlot(tessellator, tileEntity, 2);
		bufferBuilder.pos(1.01, 0, 0).endVertex();
		bufferBuilder.pos(1.01, 1, 0).endVertex();
		bufferBuilder.pos(1.01, 1, 1).endVertex();
		bufferBuilder.pos(1.01, 0, 1).endVertex();
		tessellator.draw();

		// Facing west face
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bindTextureForSlot(tessellator, tileEntity, 3);
		bufferBuilder.pos(0 - 0.01, 0, 0).endVertex();
		bufferBuilder.pos(0 - 0.01, 0, 1).endVertex();
		bufferBuilder.pos(0 - 0.01, 1, 1).endVertex();
		bufferBuilder.pos(0 - 0.01, 1, 0).endVertex();
		tessellator.draw();

		/*GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableAlpha();*/
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private void bindTextureForSlot(Tessellator tessellator, DisplayerBlockTileEntity displayerBlockTileEntity, int slot) {
		ItemStack stack = displayerBlockTileEntity.getStackInSlot(slot);

		if (Tools.isValidCard(stack)) {
			CardItem cardItem = (CardItem) stack.getItem();
			CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

			if (cStruct == null || cStruct.getDynamicTexture() == null) { // Card not registered or unregistered illustration, use item image instead
				bindTexture(new ResourceLocation(Reference.MODID + ":textures/items/item_card_" + Rarity.toString(cardItem.getCardRarity()).toLowerCase() + ".png"));
			} else {
				cStruct.preloadResource(instance.renderEngine);
				bindTexture(cStruct.getResourceLocation());
			}

			tessellator.getBuffer().color(1f, 1f, 1f, 1f);
		} else
			tessellator.getBuffer().color(1f, 1f, 1f, 0f);
	}
}
