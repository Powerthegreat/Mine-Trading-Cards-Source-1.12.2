package com.is.mtc.binder;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import com.is.mtc.root.Tools;

public class BinderItemInventory implements IInventory {

	private ItemStack[] content;
	private ItemStack container;

	/*-*/

	public BinderItemInventory(ItemStack container) {
		this.container = container;
		BinderItem.testNBT(container);

		readFromNBT(container.getTagCompound());
	}

	/*-*/

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		content = new ItemStack[getSizeInventory()];

		Arrays.fill(content, null);
		for (int i = 0; nbttaglist != null && i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getInteger("Slot");

			if (j >= 0 && j < content.length) {
				ItemStack item = new ItemStack((Item) null);
				item.setTagCompound(nbttagcompound1);
				content[j] = item;
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < content.length; ++i) {
			if (content[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setInteger("Slot", i);
				int j = nbttagcompound1.getInteger("Slot");

				content[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
	}

	/*-*/

	public static int getTotalPages() {
		return 64;
	}

	public static int getStacksPerPage() {
		return 8;
	}

	public int getSizeInventory() { // 64x8 = 512 slots
		return getTotalPages() * getStacksPerPage();
	}

	public boolean isEmpty() {
		return false;
	}

	public ItemStack getStackInSlot(int slot) {
		return content[slot];
	}

	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null) {
			if (stack.getCount() <= amount) // We set the content to null, since we'll remove more than there is in the slot
				setInventorySlotContents(slot, null);
			else
				stack = stack.splitStack(amount); // Remove 'amount' from the stack
		}

		return stack;
	}

	public ItemStack removeStackFromSlot(int slot) {
		return getStackInSlot(slot);
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	public void setInventorySlotContents(int slot, ItemStack stack) {
		content[slot] = stack;
	}

	/*-*/

	public String getName() {
		return "binder_item_inventory";
	}

	public boolean hasCustomName() {
		return false;
	}

	/*-*/

	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	/*-*/

	public void openInventory(EntityPlayer player) {

	}

	public void closeInventory(EntityPlayer player) {

	}

	/*-*/

	public boolean isItemValidForSlot(int slot, ItemStack stack) { // Is 'ItemStack' is valid for slot
		return Tools.isValidCard(stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
}
