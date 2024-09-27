package com.is.mtc.binder;

import com.is.mtc.root.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BinderItemInventory implements ICapabilitySerializable<INBT> {
	private static final int PAGES = 64;
	private static final int STACKSPERPAGE = 8;
	private IItemHandler binderInventory;
	private final LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(this::getCachedInventory);

	public BinderItemInventory() {
	}

	public static int getTotalPages() {
		return PAGES;
	}

	public static int getStacksPerPage() {
		return STACKSPERPAGE;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (LazyOptional<T>) lazyInventory;
		}
		return LazyOptional.empty();
	}

	@Override
	public INBT serializeNBT() {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedInventory(), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedInventory(), null, nbt);
	}

	private IItemHandler getCachedInventory() {
		if (binderInventory == null) {
			binderInventory = new BinderItemStackHandler(getSlots());
		}

		return binderInventory;
	}

	public int getSlots() { // 64x8 = 512 slots
		return getTotalPages() * getStacksPerPage();
	}

	static class BinderItemStackHandler extends ItemStackHandler {
		BinderItemStackHandler(int size) {
			super(size);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return Tools.isValidCard(stack);
		}
	}
}
