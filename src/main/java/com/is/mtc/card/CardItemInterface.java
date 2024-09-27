package com.is.mtc.card;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.util.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CardItemInterface extends Screen {
	public static final TranslationTextComponent title = new TranslationTextComponent("gui." + Reference.MODID + ".card_screen");
	private static final int UI_WIDTH = 224, UI_HEIGHT = 224;
	private CardStructure cStruct;
	private ItemStack stack;

	public CardItemInterface(ItemStack stack) {
		super(title);
		this.stack = stack;
		cStruct = Databank.getCardByCDWD(stack.getTag() != null ? stack.getTag().getString("cdwd") : null);
	}

	@Override
	public void render(MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		int dpx = (width - CardItemInterface.UI_WIDTH) / 2, dpy = (height - CardItemInterface.UI_HEIGHT) / 2;

		renderBackground(matrixStack);
		if (CardStructure.isValidCStructAsset(cStruct, stack)) {
			Minecraft.getInstance().getTextureManager().bind(cStruct.getResourceLocations().get(stack.getTag().getInt("assetnumber")));

			blit(matrixStack,
					/* Start position on screen */ dpx, dpy,
					/* Start position in source image */ 0, 0,
					/* Size on screen */ CardItemInterface.UI_WIDTH, CardItemInterface.UI_HEIGHT,
					/* Scale within drawing */ CardItemInterface.UI_WIDTH, CardItemInterface.UI_HEIGHT);
		}
		super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
