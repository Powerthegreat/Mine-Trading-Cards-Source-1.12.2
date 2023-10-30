package com.is.mtc.binder;

import com.is.mtc.root.CardSlot;
import com.is.mtc.root.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BinderItemContainer extends Container {
	private static final int offsetBinderX = 44, offsetBinderY = 44; // Top left, for card slots
	private static final int offsetInv3RowsX = 41, offsetInv3RowsY = 140; // Inventory pos
	private static final int offsetHotbarX = 41, offsetHotbarY = 198; // Hotbar pos

	private ItemStackHandler binderInventory;
	private ItemStack binderStack;

	public BinderItemContainer(InventoryPlayer inventory, ItemStack binderStack) {
		this.binderStack = binderStack;
		binderInventory = (ItemStackHandler) binderStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		BinderItem.testNBT(binderStack);
		readFromNBT(binderStack.getTagCompound());

		BinderItem.testNBT(binderStack);
		inventorySlots.clear();

		for (int i = 0; i < 9; i++) // Toolbar
			addSlotToContainer(new Slot(inventory, i, offsetHotbarX + i * 18, offsetHotbarY));

		for (int i = 0; i < 3; i++) // Player inv
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, /* Slot number + the toolbar size */offsetInv3RowsX + j * 18, offsetInv3RowsY + i * 18));

		// Creating card slots by slot index - NOT slot number
		for (int idx = 0; idx < BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages(); idx++) {
			int slot = idx % 8; // Slot
			int page = idx / 8; // Page
			int column = slot % 4;
			int row = slot / 4;

			addSlotToContainer(new CardSlot(binderStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), idx, /* New card slot with binderItemInventory, slot index */offsetBinderX + column * 58, offsetBinderY + row * 64)); // and slot coords
		}
	}

	public ItemStack getCardStackAtIndex(int index) {
		return inventorySlots.get(index + 36).getStack(); // Adding player's inventory size
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int providerSlotIndex) {
		Slot providerSlot = inventorySlots.get(providerSlotIndex); // Slot from where the stack comes from
		ItemStack providedStack; // Stack that is to be moved
		int binderPage;
		int tmp;

		BinderItem.testNBT(player.getActiveItemStack());
		binderPage = BinderItem.getCurrentPage(binderStack);

		if (providerSlot == null || !providerSlot.getHasStack())
			return ItemStack.EMPTY;
		providedStack = providerSlot.getStack();


		if (providerSlotIndex >= 36) { // Comes from the binder

			if (!mergeItemStack(providedStack, 0, 36, false))
				return ItemStack.EMPTY;

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? ItemStack.EMPTY : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		} else { // From inv to binder
			int mode = binderStack.getTagCompound().getInteger("mode_mtc");

			if (!Tools.isValidCard(providedStack))
				return ItemStack.EMPTY;

			switch (mode) {
				case BinderItem.MODE_STD:
					if (!mergeItemStack(providedStack, 36 + (binderPage * BinderItemInventory.getStacksPerPage()),
							36 + BinderItemInventory.getStacksPerPage() + (binderPage * BinderItemInventory.getStacksPerPage()), false))
						return ItemStack.EMPTY;
					break;
				case BinderItem.MODE_FIL:
					if (!mergeItemStack(providedStack, 36 + (binderPage * BinderItemInventory.getStacksPerPage()),
							36 + (BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages()), false))
						return ItemStack.EMPTY;
					break;
				/*case BinderItem.MODE_PLA:
				CardStructure cs = Databank.getCardByCDWD(providerSlot.getStack().stackTagCompound.getString("cdwd"));

				if (cs == null  || cs.numeral >= BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages() ||
						!mergeItemStack(providedStack, 36 + cs.numeral - 1, 36 + cs.numeral, false)) /// Note: Works. But player and server should use the same editions for better results
					return null;
				break;*/
			}

			tmp = providedStack.getCount();
			providerSlot.putStack(tmp < 1 ? ItemStack.EMPTY : providedStack); // Inform the slot about some changes
			providerSlot.onSlotChanged();
		}

		return ItemStack.EMPTY;
	}

	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {

		if (binderStack.getTagCompound() == null) // Invalid binder
			return ItemStack.EMPTY;

		if (slot == player.inventory.currentItem) // Can't slot click on the binder
			return ItemStack.EMPTY;

		return super.slotClick(slot, dragType, clickType, player);
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public int getCurrentPage() {
		return BinderItem.getCurrentPage(binderStack);
	}

	public void onContainerClosed(EntityPlayer player) {
		if (binderStack != null && binderStack.getTagCompound() != null)
			writeToNBT(binderStack.getTagCompound()); // Save data

		super.onContainerClosed(player);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		binderInventory.deserializeNBT(nbt.getCompoundTag("Items"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("Items", binderInventory.serializeNBT());
	}

	public ItemStack getBinderStack() {
		return binderStack;
	}
}
