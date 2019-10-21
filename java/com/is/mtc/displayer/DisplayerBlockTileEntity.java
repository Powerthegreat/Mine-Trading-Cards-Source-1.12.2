package com.is.mtc.displayer;

import com.is.mtc.MineTradingCards;
import com.is.mtc.packet.MTCMessageRequestUpdateDisplayer;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class DisplayerBlockTileEntity extends TileEntity implements IItemHandlerModifiable {
	public static final int INVENTORY_SIZE = 4;
	public ItemStack[] content;

	public DisplayerBlockTileEntity() {
		content = new ItemStack[getSlots()];
		Arrays.fill(content, ItemStack.EMPTY);
	}

	public ItemStack[] getContent() {
		return content;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		content = new ItemStack[getSlots()];
		Arrays.fill(content, ItemStack.EMPTY);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
			int j = nbtTagCompound.getByte("Slot") & 255;

			if (j >= 0 && j < content.length) {
				ItemStack item = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(nbtTagCompound.getString("id").split(":")[0], nbtTagCompound.getString("id").split(":")[1])));
				item.setCount(nbtTagCompound.getInteger("Count"));
				item.setTagCompound(nbtTagCompound.getCompoundTag("tag"));
				content[j] = item;
				//inventory.setStackInSlot(j, item;
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < content.length; ++i) {
			if (!content[i].isEmpty()) {
				NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
				nbtTagCompound1.setByte("Slot", (byte) i);
				content[i].writeToNBT(nbtTagCompound1);
				nbttaglist.appendTag(nbtTagCompound1);
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

	public int getSlots() {
		return INVENTORY_SIZE;
	}

	public int getSlotLimit(int slot) {
		return 64;
	}

	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (slot < getSlots()) {
			return content[slot];
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!Tools.isValidCard(stack))
			return stack;
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		ItemStack existing = content[slot];
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
				content[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			//onContentsChanged(slot);
		}

		markDirty();
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stackToReturn = content[slot].copy();
		stackToReturn.setCount(Math.min(amount, content[slot].getCount()));
		if (!simulate) {
			content[slot].shrink(stackToReturn.getCount());
			markDirty();
		}
		return stackToReturn;
	}

	public boolean isUsableByPlayer(EntityPlayer player) { // Use standard chest formula
		return world.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		if (slot < getSlots()) {
			content[slot] = stack.copy();
			markDirty();
		}
	}

	public void onLoad() {
		if (world.isRemote) {
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessageRequestUpdateDisplayer(this));
		}
	}

	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
	}
}
