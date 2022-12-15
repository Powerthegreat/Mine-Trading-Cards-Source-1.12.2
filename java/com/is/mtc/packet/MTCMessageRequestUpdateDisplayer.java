package com.is.mtc.packet;

import com.is.mtc.displayer.DisplayerBlockTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MTCMessageRequestUpdateDisplayer implements IMessage {
	public BlockPos pos;
	public int dimension;

	public MTCMessageRequestUpdateDisplayer() {

	}

	public MTCMessageRequestUpdateDisplayer(BlockPos pos, int dimension) {
		this.pos = pos;
		this.dimension = dimension;
	}

	public MTCMessageRequestUpdateDisplayer(DisplayerBlockTileEntity tileEntity) {
		this(tileEntity.getPos(), tileEntity.getWorld().provider.getDimension());
	}

	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
	}

	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
	}
}
