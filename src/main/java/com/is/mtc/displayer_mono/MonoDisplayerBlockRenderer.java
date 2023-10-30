package com.is.mtc.displayer_mono;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import org.lwjgl.opengl.GL11;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Tools;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

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
		int displayTexture = bindTextureForSlot(tessellator, tileEntity, 0);
		if (displayTexture == -1) {
			tessellator.draw();
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.NORTH) {
			if (displayTexture == 0) {
				bufferBuilder.normal(0, 0, -1);
				bufferBuilder.pos(0, 0, 0 - 0.01).tex(1, 1).endVertex();
				bufferBuilder.pos(0, 1, 0 - 0.01).tex(1, 0).endVertex();
				bufferBuilder.pos(1, 1, 0 - 0.01).tex(0, 0).endVertex();
				bufferBuilder.pos(1, 0, 0 - 0.01).tex(0, 1).endVertex();
				tessellator.draw();
			} else if (displayTexture == 1) {
				tessellator.draw();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.translate(0.5, 0.5, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(tileEntity.getStackInSlot(0), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.translate(-0.5, -0.5, 0);
			}
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.SOUTH) {
			if (displayTexture == 0) {
				bufferBuilder.normal(0, 0, 1);
				bufferBuilder.pos(0, 0, 1.01).tex(0, 1).endVertex();
				bufferBuilder.pos(1, 0, 1.01).tex(1, 1).endVertex();
				bufferBuilder.pos(1, 1, 1.01).tex(1, 0).endVertex();
				bufferBuilder.pos(0, 1, 1.01).tex(0, 0).endVertex();
				tessellator.draw();
			} else if (displayTexture == 1) {
				tessellator.draw();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.translate(0.5, 0.5, 1);
				GlStateManager.rotate(180, 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(tileEntity.getStackInSlot(0), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.rotate(-180, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.5, -1);
			}
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.EAST) {
			if (displayTexture == 0) {
				bufferBuilder.normal(1, 0, 0);
				bufferBuilder.pos(1.01, 0, 0).tex(1, 1).endVertex();
				bufferBuilder.pos(1.01, 1, 0).tex(1, 0).endVertex();
				bufferBuilder.pos(1.01, 1, 1).tex(0, 0).endVertex();
				bufferBuilder.pos(1.01, 0, 1).tex(0, 1).endVertex();
				tessellator.draw();
			} else if (displayTexture == 1) {
				tessellator.draw();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.translate(1, 0.5, 0.5);
				GlStateManager.rotate(-90, 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(tileEntity.getStackInSlot(0), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-1, -0.5, -0.5);
			}
		} else if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().get(MonoDisplayerBlock.FACING) == EnumFacing.WEST) {
			if (displayTexture == 0) {
				bufferBuilder.normal(-1, 0, 0);
				bufferBuilder.pos(0 - 0.01, 0, 0).tex(0, 1).endVertex();
				bufferBuilder.pos(0 - 0.01, 0, 1).tex(1, 1).endVertex();
				bufferBuilder.pos(0 - 0.01, 1, 1).tex(1, 0).endVertex();
				bufferBuilder.pos(0 - 0.01, 1, 0).tex(0, 0).endVertex();
				tessellator.draw();
			} else if (displayTexture == 1) {
				tessellator.draw();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.translate(0, 0.5, 0.5);
				GlStateManager.rotate(90, 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(tileEntity.getStackInSlot(0), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.rotate(-90, 0, 1, 0);
				GlStateManager.translate(0, -0.5, -0.5);
			}
		}

		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private int bindTextureForSlot(Tessellator tessellator, MonoDisplayerBlockTileEntity monoDisplayerBlockTileEntity, int slot) {
		ItemStack stack = monoDisplayerBlockTileEntity.getStackInSlot(slot);

		if (Tools.isValidCard(stack)) {
			CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

			tessellator.getBuffer().color(1f, 1f, 1f, 1f);
			if (CardStructure.isValidCStructAsset(cStruct, stack)) {
				bindTexture(cStruct.getResourceLocations().get(stack.getTagCompound().getInteger("assetnumber")));
				return 0;
			} else { // Card not registered or unregistered illustration, use item image instead
				return 1;
			}
		} else {
			tessellator.getBuffer().color(1f, 1f, 1f, 0f);
			return -1;
		}
	}
}