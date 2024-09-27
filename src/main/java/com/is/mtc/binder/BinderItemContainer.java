package com.is.mtc.binder;

import com.is.mtc.init.MTCContainers;
import com.is.mtc.root.Tools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BinderItemContainer extends Container {
	private static final int offsetBinderX = 44, offsetBinderY = 44; // Top left, for card slots
	private static final int offsetInv3RowsX = 41, offsetInv3RowsY = 140; // Inventory pos
	private static final int offsetHotbarX = 41, offsetHotbarY = 198; // Hotbar pos

	private final ItemStackHandler binderInventory;
	private final ItemStack binderStack;

	public BinderItemContainer(int windowId, PlayerInventory playerInventory, ItemStack stack) {
		super(MTCContainers.binderContainer.get(), windowId);
		binderStack = stack;
		binderInventory = (ItemStackHandler) binderStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(NullPointerException::new);
		BinderItem.testNBT(binderStack);
		//readFromNBT(binderStack.getTag());

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			addSlot(new Slot(playerInventory, hotbarSlot, offsetHotbarX + hotbarSlot * 18, offsetHotbarY));
		}

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				addSlot(new Slot(playerInventory, column + row * 9 + 9, offsetInv3RowsX + column * 18, offsetInv3RowsY + row * 18));
			}
		}

		// Creating card slots by slot index - NOT slot number
		for (int idx = 0; idx < BinderItemInventory.getStacksPerPage() * BinderItemInventory.getTotalPages(); idx++) {
			int slot = idx % 8; // Slot
			int page = idx / 8; // Page
			int column = slot % 4;
			int row = slot / 4;

			addSlot(new CardSlotItemHandler(this, binderInventory, idx, /* New card slot with binderItemInventory, slot index */offsetBinderX + column * 58, offsetBinderY + row * 64)); // and slot coords
		}
	}

	public BinderItemContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
		this(windowId, playerInventory, playerInventory.getSelected());
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BinderItem;
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		BinderItem.testNBT(binderInventory.getStackInSlot(player.inventory.selected));
		int binderPage = BinderItem.getCurrentPage(binderStack);

		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotOriginalStack = slot.getItem();
			stack = slotOriginalStack.copy();
			if (index >= 36) {  // Comes from the binder
				if (!moveItemStackTo(slotOriginalStack, 0, 36, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				int mode = binderStack.getTag().getInt("mode_mtc");
				if (!Tools.isValidCard(stack)) {
					return ItemStack.EMPTY;
				}
				switch (mode) {
					case BinderItem.MODE_STD:
						if (!moveItemStackTo(slotOriginalStack, 36 + (binderPage * BinderItemInventory.getStacksPerPage()),
								36 + BinderItemInventory.getStacksPerPage() + (binderPage * BinderItemInventory.getStacksPerPage()), false))
							return ItemStack.EMPTY;
						break;
					case BinderItem.MODE_FIL:
						if (!moveItemStackTo(slotOriginalStack, 36 + (binderPage * BinderItemInventory.getStacksPerPage()),
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
			}

			if (slotOriginalStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return stack;
	}

	@Override
	public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player) {
		if (slot == player.inventory.selected) { // Can't slot click on the binder
			return ItemStack.EMPTY;
		}

		return super.clicked(slot, dragType, clickType, player);
	}

	public ItemStack getCardStackAtIndex(int index) {
		return slots.get(index + 36).getItem(); // Adding player's inventory size
	}

	public int getCurrentPage() {
		return BinderItem.getCurrentPage(binderStack);
	}

	public ItemStack getBinderStack() {
		return binderStack;
	}

	static class CardSlotItemHandler extends SlotItemHandler {
		BinderItemContainer container;

		CardSlotItemHandler(BinderItemContainer container, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			this.container = container;
		}

		@Override
		public boolean isActive() {
			return this.getSlotIndex() / BinderItemInventory.getStacksPerPage() == container.getCurrentPage();
		}
	}
}
