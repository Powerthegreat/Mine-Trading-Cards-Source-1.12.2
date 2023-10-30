package com.is.mtc.root;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.regex.Pattern;

public class CC_ForceCreateCard extends CC_CreateCard { // Command to create a specific card. Does not have to be an existing one

	public String getName() {
		return "mtc_force_card";
	}

	public String getUsage(ICommandSender sender) {
		return "mtc_force_card <id> <edition> <rarity_index(0-4)>";
	}

	public void execute(MinecraftServer server, ICommandSender player, String[] args) {
		World world = player.getEntityWorld();
		EntityItem spawnedEntity;
		ItemStack genStack;
		String cdwd;

		if (!world.isRemote) {

			if (args.length != 3) {
				player.sendMessage(new TextComponentString("Invalid arguments; " + getUsage(player)));
				return;
			}
			cdwd = args[0].toLowerCase() + ' ' + args[1].toLowerCase() + ' ' + args[2];

			if (!Pattern.matches("^[01234]$", args[2])) {
				player.sendMessage(new TextComponentString("<rarity_index> is invalid. Must be a number between 0 and 4"));
				return;
			}
			genStack = new ItemStack(Rarity.getAssociatedCardItem(Integer.parseInt(args[2])));

			NBTTagCompound nbtTag = new NBTTagCompound();
			nbtTag.setString("cdwd", cdwd); // Setting card
			genStack.setTagCompound(nbtTag);
			spawnedEntity = new EntityItem(world, player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ(), genStack); // Spawning card

			world.spawnEntity(spawnedEntity);
		}
	}
}
