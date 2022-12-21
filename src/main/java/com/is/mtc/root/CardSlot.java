package com.is.mtc.root;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CardSlot extends SlotItemHandler {

	public CardSlot(IItemHandler inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		return super.getStack();
	}

	public boolean isItemValid(ItemStack stack) {
		return Tools.isValidCard(stack);
	}
}
