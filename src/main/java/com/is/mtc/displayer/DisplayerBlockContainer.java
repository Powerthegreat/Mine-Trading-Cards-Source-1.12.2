package com.is.mtc.displayer;

import com.is.mtc.root.CardSlot;
import com.is.mtc.root.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class DisplayerBlockContainer extends Container {
	private DisplayerBlockTileEntity tileEntity;

	private static final int offset4SlotsX = 6, offset4SlotsY = 6; // Base points for the 4 slots
	private static final int offsetInv3RowsX = 56, offsetInv3RowsY = 8; // Inventory pos
	private static final int offsetHotbarX = 56, offsetHotbarY = 66; // Hotbar pos

	public DisplayerBlockContainer(InventoryPlayer inventoryPlayer, DisplayerBlockTileEntity tileEntity) {
		this.tileEntity = tileEntity;

		// The four slots
		addSlotToContainer(new CardSlot(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 0, offset4SlotsX, offset4SlotsY) {
			@Override
			public void onSlotChanged() {
				tileEntity.markDirty();
			}
		});
		addSlotToContainer(new CardSlot(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 1, offset4SlotsX, 18 + offset4SlotsY) {
			@Override
			public void onSlotChanged() {
				tileEntity.markDirty();
			}
		});
		addSlotToContainer(new CardSlot(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 2, offset4SlotsX, 36 + offset4SlotsY) {
			@Override
			public void onSlotChanged() {
				tileEntity.markDirty();
			}
		});
		addSlotToContainer(new CardSlot(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 3, offset4SlotsX, 54 + offset4SlotsY) {
			@Override
			public void onSlotChanged() {
				tileEntity.markDirty();
			}
		});

		for (int i = 0; i < 9; i++) // Toolbar
			addSlotToContainer(new Slot(inventoryPlayer, i,
					offsetHotbarX + i * 18, offsetHotbarY));

		for (int i = 0; i < 3; i++) // Player inv
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, // Slot number + the toolbar size
						offsetInv3RowsX + j * 18, offsetInv3RowsY + i * 18));
	}

	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUsableByPlayer(player);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int providerSlotIndex) {
		Slot providerSlot = inventorySlots.get(providerSlotIndex); // Since slots are syncs, get from self
		ItemStack providedStack;
		int tmp;

		if (providerSlot == null || !providerSlot.getHasStack())
			return ItemStack.EMPTY;
		providedStack = providerSlot.getStack();

		if (providerSlotIndex < 4) { // Comes from the displayer slots. 4 because the 4 slots were added before the inventory slots
			if (!mergeItemStack(providedStack, 4, 4 + 9, false)) // Try hotbar first.  Hotbar 4 to 13 (4 slots before, and 4 slots + hotbar size)
				if (!mergeItemStack(providedStack, 4 + 9, 4 + 9 + 27, false)) // Then inventory
					return ItemStack.EMPTY;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? ItemStack.EMPTY : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		} else { // Not from 4 slots ? then from inventory !
			if (!Tools.isValidCard(providedStack))
				return ItemStack.EMPTY;

			if (!mergeItemStack(providedStack, 0, 4, false)) // Shove the card somewhere
				return ItemStack.EMPTY;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? ItemStack.EMPTY : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		}

		return ItemStack.EMPTY;
	}
}
