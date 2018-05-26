package com.is.mtc.pack;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.MineTradingCards;
import com.is.mtc.root.Rarity;

public class PackItemBase extends Item {

	protected static final int RETRY = 5;

	public PackItemBase() {
		setCreativeTab(MineTradingCards.MODTAB);
	}

	protected void createCards(int cardRarity, int count, ArrayList<String> created) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generateACard(cardRarity);

				if (cStruct != null && !created.contains(cStruct.getCDWD())) { // ... cards was not already created. Duplicate prevention
					created.add(cStruct.getCDWD());
					break;
				}
			}
		}
	}

	protected void spawnCard(EntityPlayer player, World w, String cdwd) {

		ItemStack genStack = new ItemStack(Rarity.getAssociatedCardItem(Databank.getCardByCDWD(cdwd).getRarity()));
		EntityItem spawnedEnt;

		genStack.stackTagCompound = new NBTTagCompound();
		genStack.stackTagCompound.setString("cdwd", cdwd); // Setting card
		spawnedEnt = new EntityItem(w, player.posX, player.posY + 1, player.posZ, genStack); // Spawning card

		w.spawnEntityInWorld(spawnedEnt);
	}
}
