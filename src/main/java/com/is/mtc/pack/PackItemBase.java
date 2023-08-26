package com.is.mtc.pack;

import java.util.ArrayList;
import java.util.Random;

import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PackItemBase extends Item {

	protected static final int RETRY = 50;

	public PackItemBase() {
		setCreativeTab(MineTradingCards.MODTAB);
	}

	protected void createCards(int cardRarity, int count, ArrayList<String> created, Random random) {
		
		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generateACard(cardRarity, random);

				if (cStruct != null && !created.contains(cStruct.getCDWD())) { // ... cards was not already created. Duplicate prevention
					created.add(cStruct.getCDWD());
					break;
				}
			}
		}
	}

	protected void spawnCard(EntityPlayer player, World world, String cdwd) {
		
		ItemStack genStack = new ItemStack(Rarity.getAssociatedCardItem(Databank.getCardByCDWD(cdwd).getRarity()));
		EntityItem spawnedEnt;

		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setString("cdwd", cdwd); // Setting card
		genStack.setTagCompound(nbtTag);
		spawnedEnt = new EntityItem(world, player.posX, player.posY + 1, player.posZ, genStack); // Spawning card

		world.spawnEntity(spawnedEnt);
	}
}
