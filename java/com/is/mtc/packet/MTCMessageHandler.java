package com.is.mtc.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.is.mtc.binder.BinderItem;
import com.is.mtc.binder.BinderItemInterfaceContainer;
import com.is.mtc.root.Logs;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MTCMessageHandler implements IMessageHandler<MTCMessage, IMessage>{

	@Override
	public IMessage onMessage(MTCMessage message, MessageContext ctx) {
		EntityPlayer p = ctx.getServerHandler().playerEntity;
		ItemStack binderStack = p.getHeldItem();

		if (binderStack == null || !binderStack.hasTagCompound() || !binderStack.stackTagCompound.hasKey("page"))
			return null;

		switch (message.id) {
		case BinderItemInterfaceContainer.LESS1:
			BinderItem.changePageBy(binderStack, -1);
			break;
		case BinderItemInterfaceContainer.LESS2:
			BinderItem.changePageBy(binderStack, -4);
			break;
		case BinderItemInterfaceContainer.LESS3:
			BinderItem.changePageBy(binderStack, -8);
			break;

		case BinderItemInterfaceContainer.MORE1:
			BinderItem.changePageBy(binderStack, 1);
			break;
		case BinderItemInterfaceContainer.MORE2:
			BinderItem.changePageBy(binderStack, 4);
			break;
		case BinderItemInterfaceContainer.MORE3:
			BinderItem.changePageBy(binderStack, 8);
			break;

		case BinderItemInterfaceContainer.MODE_SWITCH:
			int mode = binderStack.stackTagCompound.getInteger("mode_mtc");

			binderStack.stackTagCompound.setInteger("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
			Logs.devLog("Server: " + mode + ">>" + binderStack.stackTagCompound.getInteger("mode_mtc"));
			break;
		}

		//((EntityPlayerMP)p).sendContainerToPlayer(p.inventoryContainer); // Update
		//p.inventoryContainer.detectAndSendChanges();
		//p.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_BINDER, p.worldObj, (int)p.posX, (int)p.posY, (int)p.posZ);

		return null;
	}
}
