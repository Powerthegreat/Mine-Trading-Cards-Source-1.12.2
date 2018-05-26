package com.is.mtc.displayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.is.mtc.root.Tools;

public class DisplayerBlockTileEntity extends TileEntity implements IInventory {
	public static final int INVENTORY_SIZE = 4;
	protected ItemStack[] content;

	/*-*/

	public DisplayerBlockTileEntity() {
		content = new ItemStack[INVENTORY_SIZE];
	}

	public ItemStack[] getContent() {
		return content;
	}

	/*-*/

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		content = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < content.length)
				content[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < content.length; ++i) {
			if (content[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				content[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
		readFromNBT(nbt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	/*@Override
	public void updateContainingBlockInfo() { // Allow data being sync on game loading
		Logs.stdLog("c2");

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // Makes the server call getDescriptionPacket for a full data sync
		markDirty();

		super.updateContainingBlockInfo();
	}*/

	/*-*/

	@Override
	public int getSizeInventory() {
		return INVENTORY_SIZE;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/*-*/

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		content[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) // Slot overflow
			stack.stackSize = getInventoryStackLimit();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null) {
			if (stack.stackSize <= amount) // We set the content to null, since we've removed everything
				setInventorySlotContents(slot, null);
			else
				stack = stack.splitStack(amount); // Remove 'amount' from the stack
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return content[slot]; // Slot >= invsize ? Means something isn't doing it's job correctly !
	}

	public ItemStack getItemStackInSlot(int slot) {
		return content[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null)
			setInventorySlotContents(slot, null);

		return stack;
	}

	/*-*/

	@Override
	public String getInventoryName() {
		return "inventory_displayer";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	/*-*/

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/*-*/

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) { // Is a valid card
		return Tools.isValidCard(stack);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer user) { // Use standard chest formula
		return worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : user.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}
}
