package com.is.mtc.village;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.is.mtc.root.MineTradingCards;

import cpw.mods.fml.common.registry.VillagerRegistry;

public class VillageHandler
implements VillagerRegistry.IVillageTradeHandler {
	public static int TRADER_ID = 7117;

	public VillageHandler() {
		VillagerRegistry vr = VillagerRegistry.instance();
		vr.registerVillagerId(TRADER_ID);
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		if (villager.getProfession() == TRADER_ID) {
			recipeList.clear();
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.iron_ingot, 1), new ItemStack(MineTradingCards.packCommon)));
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.gold_ingot, 1), new ItemStack(MineTradingCards.packUncommon)));
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.diamond, 1), new ItemStack(MineTradingCards.packRare)));
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.iron_ingot, 2), new ItemStack(MineTradingCards.packStandard)));
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.iron_ingot, 2), new ItemStack(MineTradingCards.packEdition)));
			recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.emerald, 1), new ItemStack(MineTradingCards.packAncient)));
		}
	}
}

