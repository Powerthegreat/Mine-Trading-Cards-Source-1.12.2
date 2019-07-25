package com.is.mtc.village;

public class VillageHandler {
	public static int TRADER_ID = 7117;
}

/*import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.is.mtc.MineTradingCards;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class VillageHandler implements VillagerRegistry.IVillageTradeHandler {
	public static int TRADER_ID = 7117;

	public VillageHandler() {
		VillagerRegistry vr = VillagerRegistry.instance();
		vr.registerVillagerId(TRADER_ID);
		//ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("is_mtc:card_master")).getCareer(3).addTrade(1, new CardTrade(new ItemStack(Items.IRON_INGOT, 1), new EntityVillager.PriceInfo(0, 0), ));
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		if (villager.getProfessionForge() == TRADER_ID) {
			recipeList.clear();
			recipeList.add(new MerchantRecipe(new ItemStack(Items.IRON_INGOT, 1), new ItemStack(MineTradingCards.packCommon)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_INGOT, 1), new ItemStack(MineTradingCards.packUncommon)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.DIAMOND, 1), new ItemStack(MineTradingCards.packRare)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.IRON_INGOT, 2), new ItemStack(MineTradingCards.packStandard)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.IRON_INGOT, 2), new ItemStack(MineTradingCards.packEdition)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.EMERALD, 1), new ItemStack(MineTradingCards.packAncient)));
		}
	}
}

*/