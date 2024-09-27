package com.is.mtc.displayer;

import com.is.mtc.util.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisplayerBlockGuiContainer extends ContainerScreen<DisplayerBlockContainer> {
	private static final int WIDTH = 224, HEIGHT = 90;
	private static final ResourceLocation DISPLAYER_BLOCK_BACKGROUND = new ResourceLocation(Reference.MODID, "textures/gui/ui_displayer.png");

	public DisplayerBlockGuiContainer(DisplayerBlockContainer screenContainer, PlayerInventory player, ITextComponent title) {
		super(screenContainer, player, title);

		leftPos = 0;
		topPos = 0;
		imageWidth = WIDTH;
		imageHeight = HEIGHT;
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(DISPLAYER_BLOCK_BACKGROUND);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
	}
}
