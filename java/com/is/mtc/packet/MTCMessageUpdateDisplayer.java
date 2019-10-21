package com.is.mtc.packet;

import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MTCMessageUpdateDisplayer implements IMessage {
	public BlockPos pos;
	public ItemStack[] items;
	public boolean mono;

	public MTCMessageUpdateDisplayer() {

	}

	public MTCMessageUpdateDisplayer(BlockPos pos, ItemStack[] items, Boolean mono) {
		this.pos = pos;
		this.items = items;
		this.mono = mono;
	}

	public MTCMessageUpdateDisplayer(DisplayerBlockTileEntity tileEntity) {
		this(tileEntity.getPos(), tileEntity.content, (tileEntity instanceof MonoDisplayerBlockTileEntity));
	}

	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(mono);
		for (int i = 0; i < items.length; i++) {
			ByteBufUtils.writeItemStack(buf, items[i]);
		}
	}

	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		mono = buf.readBoolean();
		int maxIndex = mono ? 1 : 4;
		items = new ItemStack[maxIndex];
		for (int i = 0; i < maxIndex; i++) {
			items[i] = ByteBufUtils.readItemStack(buf);
		}
	}
}
