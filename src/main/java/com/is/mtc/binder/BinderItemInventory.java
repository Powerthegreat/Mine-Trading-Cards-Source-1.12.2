package com.is.mtc.binder;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BinderItemInventory implements ICapabilityProvider {
	private static final int PAGES = 64;
	private static final int STACKSPERPAGE = 8;

	private ItemStackHandler inventory = new ItemStackHandler(getSlots());

	public BinderItemInventory() {
	}

	public static int getTotalPages() {
		return PAGES;
	}

	public static int getStacksPerPage() {
		return STACKSPERPAGE;
	}

	public int getSlots() { // 64x8 = 512 slots
		return getTotalPages() * getStacksPerPage();
	}

	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : null;
	}
}
