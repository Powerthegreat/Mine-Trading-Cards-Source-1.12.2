package com.is.mtc.village;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class CardTrade implements EntityVillager.ITradeList{
	public ItemStack buyingItemStack;
	public EntityVillager.PriceInfo buyingPriceInfo;
	public ItemStack sellingItemstack;
	public EntityVillager.PriceInfo sellingPriceInfo;

	public CardTrade(Item itemBought, EntityVillager.PriceInfo emeraldsBought, Item itemSold, EntityVillager.PriceInfo emeraldsSold) {
		this.buyingItemStack = new ItemStack(itemBought);
		this.buyingPriceInfo = emeraldsBought;
		this.sellingItemstack = new ItemStack(itemSold);
		this.sellingPriceInfo = emeraldsSold;
	}

	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
	{
		int i = this.buyingPriceInfo.getPrice(random);
		int j = this.sellingPriceInfo.getPrice(random);
		recipeList.add(new MerchantRecipe(new ItemStack(this.buyingItemStack.getItem(), i, this.buyingItemStack.getMetadata()), new ItemStack(Items.EMERALD), new ItemStack(this.sellingItemstack.getItem(), j, this.sellingItemstack.getMetadata())));
	}
}
