package com.is.mtc.packet;

import com.is.mtc.binder.BinderItem;
import com.is.mtc.binder.BinderItemGuiContainer;
import com.is.mtc.root.Logs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MTCMessageHandler implements IMessageHandler<MTCMessage, IMessage> {
	public IMessage onMessage(MTCMessage message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		ItemStack binderStack = player.getHeldItem(player.getActiveHand());

		if (!binderStack.hasTagCompound() || !binderStack.getTagCompound().hasKey("page"))
			return null;

		switch (message.id) {
			case BinderItemGuiContainer.LESS1:
				BinderItem.changePageBy(binderStack, -1);
				break;
			case BinderItemGuiContainer.LESS2:
				BinderItem.changePageBy(binderStack, -4);
				break;
			case BinderItemGuiContainer.LESS3:
				BinderItem.changePageBy(binderStack, -8);
				break;

			case BinderItemGuiContainer.MORE1:
				BinderItem.changePageBy(binderStack, 1);
				break;
			case BinderItemGuiContainer.MORE2:
				BinderItem.changePageBy(binderStack, 4);
				break;
			case BinderItemGuiContainer.MORE3:
				BinderItem.changePageBy(binderStack, 8);
				break;

			case BinderItemGuiContainer.MODE_SWITCH:
				int mode = binderStack.getTagCompound().getInteger("mode_mtc");

				NBTTagCompound nbtTag = binderStack.getTagCompound();
				nbtTag.setInteger("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
				//binderStack.setTagCompound(nbtTag);
				//binderStack.writeToNBT(nbtTag);
				Logs.devLog("Server: " + mode + ">>" + binderStack.getTagCompound().getInteger("mode_mtc"));
				break;
		}

		//((EntityPlayerMP)p).sendContainerToPlayer(p.inventoryContainer); // Update
		//p.inventoryContainer.detectAndSendChanges();
		//p.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_BINDER, p.worldObj, (int)p.posX, (int)p.posY, (int)p.posZ);

		return null;
	}
}
