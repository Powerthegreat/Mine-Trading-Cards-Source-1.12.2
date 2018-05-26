package com.is.mtc.root;

import java.util.List;

import net.minecraft.command.CommandTime;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.is.mtc.data_manager.Databank;

public class CC_CreateCard extends CommandTime { // Command to create an existing card

	@Override
	public String getCommandName() {
		return "mtc_card";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "mtc_card <id> <edition> <rarity_index>";
	}

	@Override
	public void processCommand(ICommandSender player, String[] args) {
		World w = player.getEntityWorld();
		EntityItem spawnedEnt;
		ItemStack genStack;
		String cdwd;

		if (!w.isRemote) {
			// Check args first

			if (args.length != 3) {
				player.addChatMessage(new ChatComponentText("Invalid arguments; " + getCommandUsage(player)));
				return;
			}
			cdwd = args[0].toLowerCase() + ' ' + args[1].toLowerCase() + ' ' + args[2];

			if (Databank.getCardByCDWD(cdwd) == null) {
				player.addChatMessage(new ChatComponentText("Unable to fetch this card. Card data not found"));
				return;
			}
			genStack = new ItemStack(Rarity.getAssociatedCardItem(Databank.getCardByCDWD(cdwd).getRarity()));

			genStack.stackTagCompound = new NBTTagCompound();
			genStack.stackTagCompound.setString("cdwd", cdwd); // Setting card
			spawnedEnt = new EntityItem(w, player.getPlayerCoordinates().posX, player.getPlayerCoordinates().posY + 1, player.getPlayerCoordinates().posZ, genStack); // Spawning card

			w.spawnEntityInWorld(spawnedEnt);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_)
	{
		return null;
	}
}
