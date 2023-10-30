package com.is.mtc.packet;

import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.IItemHandler;

public class MTCMessageUpdateDisplayer implements IMessage {
	public BlockPos pos;
	public IItemHandler items;
	public NBTTagCompound nbt;
	//public DisplayerBlockTileEntity tileEntity;
	public boolean mono;

	public MTCMessageUpdateDisplayer() {

	}

	public MTCMessageUpdateDisplayer(BlockPos pos, DisplayerBlockTileEntity tileEntity, Boolean mono) {
		this.pos = pos;
		this.nbt = tileEntity.writeToNBT(new NBTTagCompound());
		this.mono = mono;
	}

	public MTCMessageUpdateDisplayer(DisplayerBlockTileEntity tileEntity) {
		this(tileEntity.getPos(), tileEntity, (tileEntity instanceof MonoDisplayerBlockTileEntity));
	}

	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(mono);
		ByteBufUtils.writeTag(buf, nbt);
		/*for (int i = 0; i < items.getSlots(); i++) {
			ByteBufUtils.writeItemStack(buf, items.getStackInSlot(i));
		}*/
	}

	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		mono = buf.readBoolean();
		nbt = ByteBufUtils.readTag(buf);
		/*int maxIndex = mono ? 1 : 4;
		//items = new ItemStack[maxIndex];
		for (int i = 0; i < maxIndex; i++) {
			items. = ByteBufUtils.readItemStack(buf);
		}*/
	}
}
