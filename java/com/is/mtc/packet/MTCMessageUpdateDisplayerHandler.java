package com.is.mtc.packet;

import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MTCMessageUpdateDisplayerHandler implements IMessageHandler<MTCMessageUpdateDisplayer, IMessage> {
	public IMessage onMessage(MTCMessageUpdateDisplayer message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			if (message.mono) {
				MonoDisplayerBlockTileEntity tileEntity = (MonoDisplayerBlockTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
				tileEntity.content = message.items;
			} else {
				DisplayerBlockTileEntity tileEntity = (DisplayerBlockTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
				tileEntity.content = message.items;
			}
		});
		return null;
	}
}
