package com.is.mtc.root;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandTime;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.is.mtc.data_manager.Databank;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;

public class CC_CreateCard extends CommandBase { // Command to create an existing card

	public String getName() {
		return "mtc_card";
	}

	public String getUsage(ICommandSender sender) {
		return "mtc_card <id> <edition> <rarity_index>";
	}

	public void execute(MinecraftServer server, ICommandSender player, String[] args) {
		World world = player.getEntityWorld();
		EntityItem spawnedEntity;
		ItemStack genStack;
		String cdwd;

		if (!world.isRemote) {
			// Check args first

			if (args.length != 3) {

				player.sendMessage(new TextComponentString("Invalid arguments; " + getUsage(player)));
				return;
			}
			cdwd = args[0].toLowerCase() + ' ' + args[1].toLowerCase() + ' ' + args[2];

			if (Databank.getCardByCDWD(cdwd) == null) {
				player.sendMessage(new TextComponentString("Unable to fetch this card. Card data not found"));
				return;
			}
			genStack = new ItemStack(Rarity.getAssociatedCardItem(Databank.getCardByCDWD(cdwd).getRarity()));

			genStack.setTagCompound(new NBTTagCompound());
			genStack.getTagCompound().setString("cdwd", cdwd); // Setting card
			spawnedEntity = new EntityItem(world, player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ(), genStack); // Spawning card

			world.spawnEntity(spawnedEntity);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return null;
	}
}
