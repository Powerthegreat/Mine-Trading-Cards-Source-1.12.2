package com.is.mtc.displayer;

import com.is.mtc.MineTradingCards;
import com.is.mtc.packet.MTCMessageRequestUpdateDisplayer;
import com.is.mtc.packet.MTCMessageUpdateDisplayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DisplayerBlockTileEntity extends TileEntity {//implements IItemHandlerModifiable {
	public static final int INVENTORY_SIZE = 4;
	private ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE);

	public DisplayerBlockTileEntity() {
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			inventory.deserializeNBT(nbt.getCompoundTag("Items"));
			super.readFromNBT(nbt);
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("Items", inventory.serializeNBT());
		readFromNBT(nbt);
		return nbt;
	}

	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeToNBT(syncData);

		return new SPacketUpdateTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), 1, syncData);
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public void updateContainingBlockInfo() { // Allow data being sync on game loading
		world.markTileEntityForRemoval(world.getTileEntity(pos));
		//world.markBlockForUpdate(xCoord, yCoord, zCoord); // Makes the server call getDescriptionPacket for a full data sync
		markDirty();

		super.updateContainingBlockInfo();
	}

	public int getSlots() {
		return INVENTORY_SIZE;
	}

	public int getSlotLimit(int slot) {
		return 64;
	}

	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (slot < getSlots()) {
			return inventory.getStackInSlot(slot);//content[slot];
		}
		return ItemStack.EMPTY;
	}

	public void setStackIntoSlot(ItemStack stack, int slot, boolean simulate) {
		if (slot >= getSlots()) {
			return;
		}
		inventory.insertItem(slot, stack, simulate);
	}

	/*@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!Tools.isValidCard(stack))
			return stack;
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		ItemStack existing = inventory.getStackInSlot(slot);//content[slot];
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
				setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			//onContentsChanged(slot);
		}

		markDirty();
		return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit);
	}

	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stackToReturn = getStackInSlot(slot).copy();
		stackToReturn.setCount(Math.min(amount, getStackInSlot(slot).getCount()));
		if (!simulate) {
			getStackInSlot(slot).shrink(stackToReturn.getCount());
			markDirty();
		}
		return stackToReturn;
	}*/

	public boolean isUsableByPlayer(EntityPlayer player) { // Use standard chest formula
		return world.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	//public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
	//inventory.setStackInSlot(slot, stack);
		/*if (slot < getSlots()) {
			//content[slot] = stack.copy();
			markDirty();
		}*/
	//}

	public void onLoad() {
		if (world.isRemote)
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessageRequestUpdateDisplayer(this));
	}

	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
	}

	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}

	public void spinCards() {
		ItemStack tempStack = inventory.getStackInSlot(0).copy();
		inventory.setStackInSlot(0, inventory.getStackInSlot(3).copy());
		inventory.setStackInSlot(3, inventory.getStackInSlot(2).copy());
		inventory.setStackInSlot(2, inventory.getStackInSlot(1).copy());
		inventory.setStackInSlot(1, tempStack);

		markDirty();

		if (!world.isRemote)
			MineTradingCards.simpleNetworkWrapper.sendToAllAround(new MTCMessageUpdateDisplayer(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}
}
