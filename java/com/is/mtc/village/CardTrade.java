package com.is.mtc.village;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class CardTrade implements EntityVillager.ITradeList {
	public ItemStack buyingItemStack1;
	public EntityVillager.PriceInfo buying1PriceInfo;
	public ItemStack buyingItemStack2;
	public EntityVillager.PriceInfo buying2PriceInfo;
	public ItemStack sellingItemstack;
	public EntityVillager.PriceInfo sellingPriceInfo;

	public CardTrade() {

	}

	public CardTrade(Item buy1, EntityVillager.PriceInfo buy1Number, Item buy2, EntityVillager.PriceInfo buy2Number, Item sell, EntityVillager.PriceInfo sellNumber) {
		buyingItemStack1 = new ItemStack(buy1);
		buying1PriceInfo = buy1Number;
		buying2PriceInfo = buy2Number;
		sellingItemstack = new ItemStack(sell);
		sellingPriceInfo = sellNumber;
	}

	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
		int amountBought1 = buying1PriceInfo.getPrice(random);
		int amountBought2 = buying2PriceInfo.getPrice(random);
		int amountSold = sellingPriceInfo.getPrice(random);

		ItemStack itemStack1;
		ItemStack itemStack2;
		ItemStack itemStack3;

		itemStack1 = amountBought1 > 0 ? new ItemStack(buyingItemStack1.getItem(), amountSold, buyingItemStack1.getMetadata()) : ItemStack.EMPTY;
		itemStack2 = amountBought2 > 0 ? new ItemStack(buyingItemStack2.getItem(), amountSold, buyingItemStack2.getMetadata()) : ItemStack.EMPTY;
		itemStack3 = amountSold > 0 ? new ItemStack(sellingItemstack.getItem(), amountSold, sellingItemstack.getMetadata()) : ItemStack.EMPTY;

		recipeList.add(new MerchantRecipe(itemStack1, itemStack2, itemStack3));
	}
}
