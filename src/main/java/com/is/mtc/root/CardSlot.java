package com.is.mtc.root;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class CardSlot extends Slot {
	public CardSlot(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	public boolean mayPlace(ItemStack stack) {
		return Tools.isValidCard(stack);
	}
}
