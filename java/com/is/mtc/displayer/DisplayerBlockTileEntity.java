package com.is.mtc.displayer;

import com.is.mtc.root.Logs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class DisplayerBlockTileEntity extends TileEntity implements IItemHandler {
	public static final int INVENTORY_SIZE = 4;
	protected ItemStack[] content;

	/*-*/

	public DisplayerBlockTileEntity() {
		content = new ItemStack[INVENTORY_SIZE];
		Arrays.fill(content, ItemStack.EMPTY);
		/*for (int i = 0; i < 4; i++) {
			content[i] = ItemStack.EMPTY;
		}*/
	}

	public ItemStack[] getContent() {
		return content;
	}

	/*-*/

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		content = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
			int j = nbtTagCompound.getByte("Slot") & 255;

			if (j >= 0 && j < content.length)
				content[j] = new ItemStack(nbtTagCompound);
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < content.length; ++i) {
			if (content[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				content[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
		readFromNBT(nbt);
		return nbt;
	}

	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeToNBT(syncData);

		return new SPacketUpdateTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), 1, syncData);
	}

	/*public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeToNBT(syncData);

		return new SPacketUpdateTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), 1, syncData);
	}*/

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public void updateContainingBlockInfo() { // Allow data being sync on game loading
		Logs.stdLog("c2");

		world.markTileEntityForRemoval(world.getTileEntity(pos));
		//world.markBlockForUpdate(xCoord, yCoord, zCoord); // Makes the server call getDescriptionPacket for a full data sync
		markDirty();

		super.updateContainingBlockInfo();
	}

	/*-*/

	public int getSizeInventory() {
		return INVENTORY_SIZE;
	}

	/*public boolean isEmpty() {
		for (ItemStack itemstack : getContent()) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public int getInventoryStackLimit() {
		return 64;
	}*/

	/*-*/

	/*public void setInventorySlotContents(int slot, ItemStack stack) {
		content[slot] = stack;

		if (stack != null && stack.getCount() > getInventoryStackLimit()) // Slot overflow
			stack.setCount(getInventoryStackLimit());
	}*/

	/*public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null) {
			if (stack.getCount() <= amount) // We set the content to null, since we've removed everything
				setInventorySlotContents(slot, null);
			else
				stack = stack.splitStack(amount); // Remove 'amount' from the stack
		}

		return stack;
	}*/

	public ItemStack getStackInSlot(int slot) {
		return content[slot]; // Slot >= invsize ? Means something isn't doing its job correctly !
	}

	public ItemStack getItemStackInSlot(int slot) {
		return content[slot];
	}

	/*public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null)
			setInventorySlotContents(slot, null);

		return stack;
	}*/

	/*-*/

	public String getName() {
		return "inventory_displayer";
	}

	/*public boolean hasCustomName() {
		return false;
	}*/

	/*-*/

	/*public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}*/

	/*-*/

	/*public boolean isItemValidForSlot(int slot, ItemStack stack) { // Is a valid card
		return Tools.isValidCard(stack);
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {

	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {

	}*/

	public boolean isUsableByPlayer(EntityPlayer user) { // Use standard chest formula
		return world.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) == this && user.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public int getSlots() {
		return INVENTORY_SIZE;
	}

	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStack stackCopy = stack.copy();
		if (getStackInSlot(slot).getMaxStackSize() > getStackInSlot(slot).getCount()) {

		}
		return ItemStack.EMPTY;
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (content[slot].getCount() > amount) {
			ItemStack copy = content[slot].copy();
			copy.setCount(amount);
			return copy;
		}
		return content[slot].copy();
	}

	public int getSlotLimit(int slot) {
		return 64;
	}
}
