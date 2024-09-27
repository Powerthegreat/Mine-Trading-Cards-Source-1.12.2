package com.is.mtc.displayer;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Tools;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisplayerBlockRenderer extends TileEntityRenderer<DisplayerBlockTileEntity> {
	public DisplayerBlockRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(DisplayerBlockTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// North
		int displayTexture = bindTextureForSlot(tileEntity, 0);
		if (displayTexture == 0) {
			matrixStack.pushPose();
			BufferBuilder bufferBuilder = (BufferBuilder) buffer.getBuffer(RenderType.text(Databank.getCardByCDWD(tileEntity.getItem(0).getTag().getString("cdwd")).getResourceLocations().get(tileEntity.getItem(0).getTag().getInt("assetnumber"))));

			bufferBuilder.vertex(matrixStack.last().pose(), 0, 0, 0 - 0.01f)
					.color(255, 255, 255, 255)
					.uv(1, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, -1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 0, 1, 0 - 0.01f)
					.color(255, 255, 255, 255)
					.uv(1, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, -1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1, 1, 0 - 0.01f)
					.color(255, 255, 255, 255)
					.uv(0, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, -1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1, 0, 0 - 0.01f)
					.color(255, 255, 255, 255)
					.uv(0, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, -1).endVertex();

			matrixStack.popPose();
		} else if (displayTexture == 1) {
			renderItem(tileEntity.getItem(0), new double[]{0.5, 0.5, 0.0}, Vector3f.YP.rotationDegrees(0), 1, matrixStack, buffer, partialTicks, combinedLight, combinedOverlay);
		}

		// South
		displayTexture = bindTextureForSlot(tileEntity, 1);
		if (displayTexture == 0) {
			matrixStack.pushPose();
			BufferBuilder bufferBuilder = (BufferBuilder) buffer.getBuffer(RenderType.text(Databank.getCardByCDWD(tileEntity.getItem(1).getTag().getString("cdwd")).getResourceLocations().get(tileEntity.getItem(1).getTag().getInt("assetnumber"))));

			bufferBuilder.vertex(matrixStack.last().pose(), 0, 0, 1.01f)
					.color(255, 255, 255, 255)
					.uv(0, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, 1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1, 0, 1.01f)
					.color(255, 255, 255, 255)
					.uv(1, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, 1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1, 1, 1.01f)
					.color(255, 255, 255, 255)
					.uv(1, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, 1).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 0, 1, 1.01f)
					.color(255, 255, 255, 255)
					.uv(0, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 0, 0, 1).endVertex();

			matrixStack.popPose();
		} else if (displayTexture == 1) {
			renderItem(tileEntity.getItem(1), new double[]{0.5, 0.5, 1.0}, Vector3f.YP.rotationDegrees(180), 1, matrixStack, buffer, partialTicks, combinedLight, combinedOverlay);
		}

		// East
		displayTexture = bindTextureForSlot(tileEntity, 2);
		if (displayTexture == 0) {
			matrixStack.pushPose();
			BufferBuilder bufferBuilder = (BufferBuilder) buffer.getBuffer(RenderType.text(Databank.getCardByCDWD(tileEntity.getItem(2).getTag().getString("cdwd")).getResourceLocations().get(tileEntity.getItem(2).getTag().getInt("assetnumber"))));

			bufferBuilder.vertex(matrixStack.last().pose(), 1.01f, 0, 0)
					.color(255, 255, 255, 255)
					.uv(1, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1.01f, 1, 0)
					.color(255, 255, 255, 255)
					.uv(1, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1.01f, 1, 1)
					.color(255, 255, 255, 255)
					.uv(0, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 1.01f, 0, 1)
					.color(255, 255, 255, 255)
					.uv(0, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), 1, 0, 0).endVertex();

			matrixStack.popPose();
		} else if (displayTexture == 1) {
			renderItem(tileEntity.getItem(2), new double[]{1.0, 0.5, 0.5}, Vector3f.YP.rotationDegrees(90), 1, matrixStack, buffer, partialTicks, combinedLight, combinedOverlay);
		}

		// West
		displayTexture = bindTextureForSlot(tileEntity, 3);
		if (displayTexture == 0) {
			matrixStack.pushPose();
			BufferBuilder bufferBuilder = (BufferBuilder) buffer.getBuffer(RenderType.text(Databank.getCardByCDWD(tileEntity.getItem(3).getTag().getString("cdwd")).getResourceLocations().get(tileEntity.getItem(3).getTag().getInt("assetnumber"))));

			bufferBuilder.vertex(matrixStack.last().pose(), 0 - 0.01f, 0, 0)
					.color(255, 255, 255, 255)
					.uv(0, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), -1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 0 - 0.01f, 0, 1)
					.color(255, 255, 255, 255)
					.uv(1, 1).uv2(combinedLight)
					.normal(matrixStack.last().normal(), -1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 0 - 0.01f, 1, 1)
					.color(255, 255, 255, 255)
					.uv(1, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), -1, 0, 0).endVertex();
			bufferBuilder.vertex(matrixStack.last().pose(), 0 - 0.01f, 1, 0)
					.color(255, 255, 255, 255)
					.uv(0, 0).uv2(combinedLight)
					.normal(matrixStack.last().normal(), -1, 0, 0).endVertex();

			matrixStack.popPose();
		} else if (displayTexture == 1) {
			renderItem(tileEntity.getItem(3), new double[]{0.0, 0.5, 0.5}, Vector3f.YP.rotationDegrees(270), 1, matrixStack, buffer, partialTicks, combinedLight, combinedOverlay);
		}
	}

	private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, float scale, MatrixStack matrixStack, IRenderTypeBuffer buffer, float partialTicks, int combinedLight, int combinedOverlay) {
		if (stack == ItemStack.EMPTY || stack.getItem() == Items.AIR) {
			return;
		}

		matrixStack.pushPose();
		matrixStack.translate(translation[0], translation[1], translation[2]);
		matrixStack.mulPose(rotation);
		matrixStack.scale(scale, scale, scale);

		IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null);
		Minecraft.getInstance().getItemRenderer().render(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay, itemModel);
		matrixStack.popPose();
	}

	private int bindTextureForSlot(DisplayerBlockTileEntity displayerBlockTileEntity, int slot) {
		ItemStack stack = displayerBlockTileEntity.getItem(slot);

		if (Tools.isValidCard(stack)) {
			CardStructure cStruct = Databank.getCardByCDWD(stack.getTag().getString("cdwd"));

			if (CardStructure.isValidCStructAsset(cStruct, stack)) {
				return 0;
			} else { // Card not registered or unregistered illustration, use item image instead
				return 1;
			}
		} else {
			return -1;
		}
	}
}
