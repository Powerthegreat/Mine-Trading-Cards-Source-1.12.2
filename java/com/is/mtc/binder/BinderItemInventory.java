package com.is.mtc.binder;

import com.is.mtc.root.Tools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class BinderItemInventory implements IItemHandlerModifiable {
	public boolean hasLoaded;
	private ItemStack[] contents;
	private ItemStack binderStack;

	public BinderItemInventory(ItemStack binder) {
		binderStack = binder;
		BinderItem.testNBT(binderStack);

		readFromNBT(binderStack.getTagCompound());
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList nbtTagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		contents = new ItemStack[getSlots()];

		Arrays.fill(contents, ItemStack.EMPTY);
		if (nbtTagList.tagCount() > 0) {
			for (int i = 0; i < nbtTagList.tagCount(); i++) {
				NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
				int j = nbtTagCompound.getInteger("Slot");

				if (j >= 0 && j < contents.length) {
					ItemStack item = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(nbtTagCompound.getString("id").split(":")[0], nbtTagCompound.getString("id").split(":")[1])));
					item.setCount(nbtTagCompound.getInteger("Count"));
					item.setTagCompound(nbtTagCompound.getCompoundTag("tag"));
					contents[j] = item;
				}
			}
		}
		hasLoaded = true;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < contents.length; ++i) {
			if (contents[i] != ItemStack.EMPTY) {
				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				nbtTagCompound.setInteger("Slot", i);

				contents[i].writeToNBT(nbtTagCompound);
				if (!nbtTagCompound.getString("id").equals("minecraft:air")) {
					nbttaglist.appendTag(nbtTagCompound);
				}
			}
		}

		nbt.setTag("Items", nbttaglist);
	}

	public static int getTotalPages() {
		return 64;
	}

	public static int getStacksPerPage() {
		return 8;
	}

	public int getSlots() { // 64x8 = 512 slots
		return getTotalPages() * getStacksPerPage();
	}

	public ItemStack getStackInSlot(int slot) {
		if (slot < getSlots()) {
			return contents[slot];
		}
		return ItemStack.EMPTY;
	}

	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!Tools.isValidCard(stack))
			return stack;
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		ItemStack existing = contents[slot];
		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				contents[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			//onContentsChanged(slot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
		/*ItemStack existingStack = contents[slot];
		int amountToAdd;
		if (!existingStack.isEmpty()) {
			if (existingStack.getCount() >= Math.min(existingStack.getMaxStackSize(), getSlotLimit(slot)))
				return stack;

			if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack))
				return stack;

			amountToAdd = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - existingStack.getCount();

			if (stack.getCount() <= amountToAdd) {
				if (!simulate) {
					//ItemStack copy = stack.copy();
					//copy.grow(existingStack.getCount());
					//contents[slot] = copy;
					existingStack.grow(stack.getCount());
					writeToNBT(binderStack.getTagCompound());
					//getInv().markDirty();
				}

				return ItemStack.EMPTY;
			} else {
				// copy the stack to not modify the original one
				ItemStack newStack = stack.copy();
				if (!simulate) {
					//ItemStack copy = newStack.splitStack(amountToAdd);
					//copy.grow(existingStack.getCount());
					//contents[slot] = copy;
					existingStack.grow(newStack.splitStack(amountToAdd));
					writeToNBT(binderStack.getTagCompound());
					//getInv().markDirty();
					return newStack;
				} else {
					newStack.shrink(amountToAdd);
					return newStack;
				}
			}
		} else {
			amountToAdd = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (amountToAdd < stack.getCount()) {
				// copy the stack to not modify the original one
				ItemStack newStack = stack.copy();
				if (!simulate) {
					contents[slot] = newStack.splitStack(amountToAdd);
					writeToNBT(binderStack.getTagCompound());
					//getInv().markDirty();
					return newStack;
				} else {
					newStack.shrink(amountToAdd);
					return newStack;
				}
			}
			else {
				if (!simulate) {
					ItemStack copy = stack.copy();
					contents[slot] = copy;
					writeToNBT(binderStack.getTagCompound());
					//getInv().markDirty();
				}
				return ItemStack.EMPTY;
			}
		}*/
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stackToReturn = contents[slot].copy();
		stackToReturn.setCount(Math.min(amount, contents[slot].getCount()));
		if (!simulate) {
			contents[slot].shrink(stackToReturn.getCount());
			writeToNBT(binderStack.getTagCompound());
		}
		return stackToReturn;
	}

	public int getSlotLimit(int slot) {
		return 64;
	}

	public ItemStack getBinderStack() {
		return binderStack;
	}

	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		if (slot < getSlots()) {
			contents[slot] = stack.copy();
			writeToNBT(binderStack.getTagCompound());
		}
	}
}
