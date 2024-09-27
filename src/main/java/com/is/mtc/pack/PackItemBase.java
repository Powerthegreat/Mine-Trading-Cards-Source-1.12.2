package com.is.mtc.pack;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class PackItemBase extends Item {

	protected static final int RETRY = 50;

	public PackItemBase(Properties properties) {
		super(properties);
	}

	protected void createCards(int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generateACard(cardRarity, new Random()); // Using new Random() because world random can cause issues generating cards

				if (cStruct != null && !created.contains(cStruct.getCDWD())) { // ... card was not already created. Duplicate prevention
					created.add(cStruct.getCDWD());
					break;
				}
			}
		}
	}

	protected void spawnCard(PlayerEntity player, World world, String cdwd) {

		ItemStack genStack = new ItemStack(Rarity.getAssociatedCardItem(Databank.getCardByCDWD(cdwd).getRarity()));
		ItemEntity spawnedEnt;

		CompoundNBT nbtTag = new CompoundNBT();
		nbtTag.putString("cdwd", cdwd); // Setting card
		if (Databank.getCardByCDWD(cdwd).getResourceLocations().size() > 1)
			nbtTag.putInt("assetnumber", Tools.randInt(0, Databank.getCardByCDWD(cdwd).getResourceLocations().size(), world.getRandom()));
		genStack.setTag(nbtTag);
		spawnedEnt = new ItemEntity(world, player.position().x, player.position().y + 1, player.position().z, genStack); // Spawning card

		world.addFreshEntity(spawnedEnt);
	}
}
