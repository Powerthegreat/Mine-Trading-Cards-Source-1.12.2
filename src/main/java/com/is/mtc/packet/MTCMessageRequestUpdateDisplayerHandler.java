package com.is.mtc.packet;

import com.is.mtc.displayer.DisplayerBlockTileEntity;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MTCMessageRequestUpdateDisplayerHandler implements IMessageHandler<MTCMessageRequestUpdateDisplayer, IMessage> {
	public MTCMessageUpdateDisplayer onMessage(MTCMessageRequestUpdateDisplayer message, MessageContext ctx) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
		DisplayerBlockTileEntity tileEntity = (DisplayerBlockTileEntity) world.getTileEntity(message.pos);
		if (tileEntity != null) {
			return new MTCMessageUpdateDisplayer(tileEntity);
		} else {
			return null;
		}
	}
}
