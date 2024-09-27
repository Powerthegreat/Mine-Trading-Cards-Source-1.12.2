package com.is.mtc.packet;

import com.is.mtc.binder.BinderItem;
import com.is.mtc.binder.BinderItemGuiContainer;
import com.is.mtc.root.Logs;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MTCMessage {
	public int id;

	public MTCMessage() {
	}

	public MTCMessage(int id) {
		this.id = id;
	}

	public static MTCMessage fromBytes(PacketBuffer buf) {
		MTCMessage newMessage = new MTCMessage(buf.readInt());
		//id = buf.readInt();
		return newMessage;
	}

	public static void onMessage(MTCMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			ItemStack binderStack = player.getItemInHand(player.getUsedItemHand());

			if (!binderStack.hasTag() || !binderStack.getTag().contains("page"))
				return;

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
					int mode = binderStack.getTag().getInt("mode_mtc");

					CompoundNBT nbtTag = binderStack.getTag();
					nbtTag.putInt("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
					Logs.devLog("Server: " + mode + ">>" + binderStack.getTag().getInt("mode_mtc"));
					break;
			}
		});

		ctx.get().setPacketHandled(true);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(id);
	}
}
