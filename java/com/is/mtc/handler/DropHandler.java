package com.is.mtc.handler;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.is.mtc.root.MineTradingCards;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DropHandler {

	public static boolean CAN_DROP_ANIMAL = false;
	public static boolean CAN_DROP_PLAYER = false;
	public static boolean CAN_DROP_MOB = true;

	// 1 chance out of DROP_RATE_X (test order)
	public static int DROP_RATE_COM = 16; // (7)
	public static int DROP_RATE_UNC = 32; // (6)
	public static int DROP_RATE_RAR = 48; // (5)
	public static int DROP_RATE_ANC = 64; // (2)
	public static int DROP_RATE_LEG = 256; // (1)

	public static int DROP_RATE_STD = 56; // (4)
	public static int DROP_RATE_EDT = 56; // (3)

	private void addDrop(Item drop, LivingDropsEvent event, int count) {
		ItemStack itemToDrop = new ItemStack(drop);

		event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX,
				event.entity.posY, event.entity.posZ, itemToDrop));
	}

	private void addDrop(Item drop, LivingDropsEvent event) {
		addDrop(drop, event, 1);
	}

	private void testDrop(int rate, Item drop, LivingDropsEvent event) {
		Random r = new Random();
		int dv = r.nextInt(rate);

		if (rate > 0 && dv == 0)
			addDrop(drop, event);
	}

	@SubscribeEvent
	public void onEvent(LivingDropsEvent event) {
		if (!(event.entity instanceof EntityLiving)) // Not a known living entity
			return;

		if (!CAN_DROP_MOB && event.entity instanceof EntityMob)
			return;

		if (!CAN_DROP_ANIMAL && event.entity instanceof EntityAnimal)
			return;

		if (!CAN_DROP_PLAYER && event.entity instanceof EntityPlayer)
			return;

		if (event.entity instanceof EntityDragon) { // 18 packs
			addDrop(MineTradingCards.packLegendary, event);
			addDrop(MineTradingCards.packAncient, event, 2);
			addDrop(MineTradingCards.packRare, event, 3);
			addDrop(MineTradingCards.packUncommon, event, 5);
			addDrop(MineTradingCards.packCommon, event, 7);
		}

		if (event.entity instanceof EntityWither) { // 18 packs
			testDrop(4, MineTradingCards.packLegendary, event); // 1 chance on 4 to drop a pl
			addDrop(MineTradingCards.packAncient, event, 1);
			addDrop(MineTradingCards.packRare, event, 2);
			addDrop(MineTradingCards.packUncommon, event, 3);
			addDrop(MineTradingCards.packCommon, event, 3);
		}

		testDrop(DROP_RATE_LEG, MineTradingCards.packLegendary, event); // Legendary (leg)
		testDrop(DROP_RATE_ANC, MineTradingCards.packAncient, event); // Ancient (anc)
		testDrop(DROP_RATE_EDT, MineTradingCards.packEdition, event); // Edition (edt)
		testDrop(DROP_RATE_STD, MineTradingCards.packStandard, event); // Standard (std)
		testDrop(DROP_RATE_RAR, MineTradingCards.packRare, event); // Rare (rar)
		testDrop(DROP_RATE_UNC, MineTradingCards.packUncommon, event); // Uncommon (unc)
		testDrop(DROP_RATE_COM, MineTradingCards.packCommon, event); // Common (com)
	}
}
