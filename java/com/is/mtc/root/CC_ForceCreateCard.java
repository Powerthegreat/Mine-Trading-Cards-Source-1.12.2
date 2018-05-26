package com.is.mtc.root;

import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CC_ForceCreateCard extends CC_CreateCard { // Command to create a specific card. Does not have to be an existing one

	@Override
	public String getCommandName() {
		return "mtc_force_card";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "mtc_force_card <id> <edition> <rarity_index(0-4)>";
	}

	@Override
	public void processCommand(ICommandSender player, String[] args) {
		World w = player.getEntityWorld();
		EntityItem spawnedEnt;
		ItemStack genStack;
		String cdwd;

		if (!w.isRemote) {

			if (args.length != 3) {
				player.addChatMessage(new ChatComponentText("Invalid arguments; " + getCommandUsage(player)));
				return;
			}
			cdwd = args[0].toLowerCase() + ' ' + args[1].toLowerCase() + ' ' + args[2];

			if (!Pattern.matches("^[01234]$", args[2])) {
				player.addChatMessage(new ChatComponentText("<rarity_index> is invalid. Must be a number between 0 and 4"));
				return;
			}
			genStack = new ItemStack(Rarity.getAssociatedCardItem(Integer.parseInt(args[2])));

			genStack.stackTagCompound = new NBTTagCompound();
			genStack.stackTagCompound.setString("cdwd", cdwd); // Setting card
			spawnedEnt = new EntityItem(w, player.getPlayerCoordinates().posX, player.getPlayerCoordinates().posY + 1, player.getPlayerCoordinates().posZ, genStack); // Spawning card

			w.spawnEntityInWorld(spawnedEnt);
		}
	}
}
