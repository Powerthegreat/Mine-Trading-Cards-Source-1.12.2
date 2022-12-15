package com.is.mtc.displayer_mono;

import com.is.mtc.displayer.DisplayerBlockTileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;

public class MonoDisplayerBlockTileEntity extends DisplayerBlockTileEntity {
	public static final int INVENTORY_SIZE = 1;

	/*-*/

	public MonoDisplayerBlockTileEntity() {
		/*content = new ItemStack[INVENTORY_SIZE];
		Arrays.fill(content, ItemStack.EMPTY);*/
	}

	public int getSlots() {
		return INVENTORY_SIZE;
	}

	/*-*/

	/*public void readFromNBT(NBTTagCompound nbt) {
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
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < content.length; ++i) {
			if (!content[i].isEmpty()) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				content[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
		readFromNBT(nbt);
		return nbt;
	}*/

	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeToNBT(syncData);

		return new SPacketUpdateTileEntity(new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ()), 1, syncData);
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	/*@Override
	public void updateContainingBlockInfo() { // Allow data being sync on game loading

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // Makes the server call getDescriptionPacket for a full data sync
		markDirty();

		super.updateContainingBlockInfo();
	}*/
}