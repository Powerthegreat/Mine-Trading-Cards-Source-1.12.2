package com.is.mtc.displayer_mono;

import com.is.mtc.root.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MonoDisplayerBlockContainer extends Container {
	private MonoDisplayerBlockTileEntity tileEntity;

	private static final int offsetInv3RowsX = 56, offsetInv3RowsY = 8; // Inventory pos
	private static final int offsetHotbarX = 56, offsetHotbarY = 66; // Hotbar pos

	public MonoDisplayerBlockContainer(InventoryPlayer inventoryPlayer, MonoDisplayerBlockTileEntity tileEntity) {
		this.tileEntity = tileEntity;
		//IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

		//addSlotToContainer(new CardSlot(tileEntity, 0, 21, 26));

		for (int i = 0; i < 9; i++) // Toolbar
			addSlotToContainer(new Slot(inventoryPlayer, i,
					offsetHotbarX + i * 18, offsetHotbarY));

		for (int i = 0; i < 3; i++) // Player inv
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, // Slot number + the toolbar size
						offsetInv3RowsX + j * 18, offsetInv3RowsY + i * 18));
	}

	@Override
	public boolean canInteractWith(EntityPlayer user) {
		return tileEntity.isUsableByPlayer(user);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int providerSlotIndex) {
		Slot providerSlot = inventorySlots.get(providerSlotIndex); // Since slots are syncs, get from self
		ItemStack providedStack = null;
		int tmp;

		if (providerSlot == null || !providerSlot.getHasStack())
			return null;
		providedStack = providerSlot.getStack();

		if (providerSlotIndex == 0) { // Comes from the displayer slots
			if (!mergeItemStack(providedStack, 1, 1 + 9, false)) //
				if (!mergeItemStack(providedStack, 1 + 9, 1 + 9 + 27, false)) // Then inventory
					return null;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? null : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		} else { // Not from slot ? then from inventory !
			if (!Tools.isValidCard(providedStack))
				return null;

			if (!mergeItemStack(providedStack, 0, 1, false)) // Shove the card somewhere
				return null;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? null : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		}

		return null;
	}
}