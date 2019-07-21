package com.is.mtc.binder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.is.mtc.root.CardSlot;
import com.is.mtc.root.Tools;

public class BinderItemContainer extends Container {
	private static final int offsetBinderX = 44, offsetBinderY = 44; // Top left

	private static final int offsetInv3RowsX = 41, offsetInv3RowsY = 140; // Inventory pos
	private static final int offsetHotbarX = 41, offsetHotbarY = 198; // Hotbar pos

	/*-*/

	private BinderItemInventory bii;
	private ItemStack binderStack;
	private InventoryPlayer invP;

	/*-*/

	public BinderItemContainer(InventoryPlayer invP, BinderItemInventory bii) {
		binderStack = invP.getCurrentItem();
		this.invP = invP;
		this.bii = bii;

		BinderItem.testNBT(binderStack);

		inventorySlots.clear();
		for (int i = 0; i < 9; i++) // Toolbar
			addSlotToContainer(new Slot(invP, i,
					offsetHotbarX + i * 18, offsetHotbarY));

		for (int i = 0; i < 3; i++) // Player inv
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(invP, j + i * 9 + 9, // Slot number + the toolbar size
						offsetInv3RowsX + j * 18, offsetInv3RowsY + i * 18));

		// Note that slot index is different from slot number !!
		for (int idx = 0; idx < BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages(); ++idx) {
			int i = idx % 8; // Slot
			int j = idx / 8; // Page
			int col = i % 4;
			int row = i / 4;

			addSlotToContainer(new CardSlot(bii, idx, // New card slot with bii, slot index
					offsetBinderX + col * 58, offsetBinderY + row * 64)); // and slot coords
		}
	}

	public ItemStack getCardStackAtIndex(int idx) {
		return inventorySlots.get(idx + 36).getStack(); // +Inventory size
	}

	public ItemStack getBinderStack() {
		return binderStack;
	}

	/*-*/

	public ItemStack transferStackInSlot(EntityPlayer player, int providerSlotIndex) {
		Slot providerSlot = inventorySlots.get(providerSlotIndex); // Slot from where the stack comes from
		ItemStack providedStack; // Stack that is to be moved
		int binderpage;
		int tmp;

		if (!(player.getActiveItemStack().getItem() instanceof BinderItem))
			return null;
		BinderItem.testNBT(player.getActiveItemStack());
		binderpage = BinderItem.getCurrentPage(player.getActiveItemStack());

		if (providerSlot == null || !providerSlot.getHasStack())
			return null;
		providedStack = providerSlot.getStack();


		if (providerSlotIndex >= 36) { // Comes from the binder

			if (!mergeItemStack(providedStack, 0, 36, false))
				return null;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? null : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		}
		else { // From inv to binder
			int mode = player.getActiveItemStack().getTagCompound().getInteger("mode_mtc");

			if (!Tools.isValidCard(providedStack))
				return null;

			switch (mode) {
			case BinderItem.MODE_STD:
				if (!mergeItemStack(providedStack, 36 + (binderpage * BinderItemInventory.getStacksPerPage()),
						36 + BinderItemInventory.getStacksPerPage() + (binderpage * BinderItemInventory.getStacksPerPage()), false))
					return null;
				break;
			case BinderItem.MODE_FIL:
				if (!mergeItemStack(providedStack, 36 + (binderpage * BinderItemInventory.getStacksPerPage()),
						36 + (BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages()), false))
					return null;
				break;
				/*case BinderItem.MODE_PLA:
				CardStructure cs = Databank.getCardByCDWD(providerSlot.getStack().stackTagCompound.getString("cdwd"));

				if (cs == null  || cs.numeral >= BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages() ||
						!mergeItemStack(providedStack, 36 + cs.numeral - 1, 36 + cs.numeral, false)) /// Note: Works. But player and server should use the same editions for better results
					return null;
				break;*/
			}

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? null : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		}

		return null;
	}

	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		ItemStack heldItem = player.getActiveItemStack();

		if (heldItem == null || heldItem.getTagCompound() == null) // Invalid binder
			return null;

		if (slot == player.inventory.currentItem) // Can't slot click on the binder
			return null;

		if (slot >= 36) {// Slot is from binder
			int binderPage = BinderItem.getCurrentPage(player.getActiveItemStack());

			slot += (binderPage * BinderItemInventory.getStacksPerPage()); // Set current slot offset then
		}
		return super.slotClick(slot, dragType, clickType, player);
	}

	/*-*/

	/*-*/

	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return bii.isUseableByPlayer(p_75145_1_);
	}

	public void onContainerClosed(EntityPlayer p_75134_1_) {
		ItemStack heldItem = p_75134_1_.getActiveItemStack();

		if (heldItem != null && heldItem.getTagCompound() != null)
			bii.writeToNBT(heldItem.getTagCompound()); // Save data
		super.onContainerClosed(p_75134_1_);
	}

	/*-*/

	public int getCurrentPage() {
		return BinderItem.getCurrentPage(binderStack);
	}
}
