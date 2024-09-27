package com.is.mtc.displayer;

import com.is.mtc.init.MTCBlocks;
import com.is.mtc.init.MTCContainers;
import com.is.mtc.root.CardSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class DisplayerBlockContainer extends Container {
	private static final int offset4SlotsX = 6, offset4SlotsY = 6; // Base points for the 4 slots
	private static final int offsetInv3RowsX = 56, offsetInv3RowsY = 8; // Inventory pos
	private static final int offsetHotbarX = 56, offsetHotbarY = 66; // Hotbar pos
	public final DisplayerBlockTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;

	public DisplayerBlockContainer(int windowId, PlayerInventory playerInventory, DisplayerBlockTileEntity tileEntity) {
		super(MTCContainers.displayerBlockContainer.get(), windowId);
		this.tileEntity = tileEntity;
		canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

		for (int i = 0; i < tileEntity.getContainerSize(); i++) {
			addSlot(new CardSlot(tileEntity, i, offset4SlotsX, offset4SlotsY + i * 18));
		}

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				addSlot(new Slot(playerInventory, column + row * 9 + 9, offsetInv3RowsX + column * 18, offsetInv3RowsY + row * 18));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			addSlot(new Slot(playerInventory, hotbarSlot, offsetHotbarX + hotbarSlot * 18, offsetHotbarY));
		}
	}

	public DisplayerBlockContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
		this(windowId, playerInventory, (DisplayerBlockTileEntity) playerInventory.player.level.getBlockEntity(data.readBlockPos()));
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(canInteractWithCallable, player, MTCBlocks.displayerBlock.get());
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotOriginalStack = slot.getItem();
			stack = slotOriginalStack.copy();
			if (index < tileEntity.getContainerSize()) {
				if (!moveItemStackTo(slotOriginalStack, tileEntity.getContainerSize(), slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotOriginalStack, 0, tileEntity.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (slotOriginalStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return stack;
	}
}
