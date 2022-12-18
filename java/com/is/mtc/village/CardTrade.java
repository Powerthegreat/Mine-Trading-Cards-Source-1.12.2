package com.is.mtc.village;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class CardTrade implements EntityVillager.ITradeList {
	private int tradeLevel;
	private float tradeChance;
	private ItemStack buyingItemStack1;
	private EntityVillager.PriceInfo buying1PriceInfo;
	private ItemStack buyingItemStack2;
	private EntityVillager.PriceInfo buying2PriceInfo;
	private ItemStack sellingItemstack;
	private EntityVillager.PriceInfo sellingPriceInfo;
	
	public int getTradeLevel() {
		return tradeLevel;
	}
	
	public CardTrade() {
	}
	
	@Nullable
	public CardTrade(int tradelevel, float tradechance, ItemStack buyitem1_stack, EntityVillager.PriceInfo buy1Number, ItemStack buyitem2_stack, EntityVillager.PriceInfo buy2Number, ItemStack sellitem_stack, EntityVillager.PriceInfo sellNumber) {
		tradeLevel = tradelevel;
		tradeChance = tradechance;
		buyingItemStack1 = buyitem1_stack;
		buying1PriceInfo = buy1Number;
		buyingItemStack2 = buyitem2_stack;
		buying2PriceInfo = buy2Number;
		sellingItemstack = sellitem_stack;
		sellingPriceInfo = sellNumber;
	}
	
	@Nullable
	public CardTrade(int tradelevel, float tradechance, ItemStack buyitem1_stack, EntityVillager.PriceInfo buy1Number, ItemStack sellitem_stack, EntityVillager.PriceInfo sellNumber) {
		tradeLevel = tradelevel;
		tradeChance = tradechance;
		buyingItemStack1 = buyitem1_stack;
		buying1PriceInfo = buy1Number;
		buyingItemStack2 = ItemStack.EMPTY;
		buying2PriceInfo = new EntityVillager.PriceInfo(0, 0);
		sellingItemstack = sellitem_stack;
		sellingPriceInfo = sellNumber;
	}
	
	@Override
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
		int amountBought1 = buying1PriceInfo.getPrice(random);
		int amountBought2 = buying2PriceInfo.getPrice(random);
		int amountSold = sellingPriceInfo.getPrice(random);
		
		// Don't add a trade if it's invalid
		if (buyingItemStack1 == ItemStack.EMPTY || amountBought1==0
				|| sellingItemstack == ItemStack.EMPTY || amountSold==0) {
			return;
		}
		
		// Don't add a trade probabilistically
		if (random.nextFloat() >= tradeChance) {
			return;
		}
		
		// Adjust itemstacks with amounts provided
		buyingItemStack1.setCount(buying1PriceInfo.getPrice(random));
		sellingItemstack.setCount(sellingPriceInfo.getPrice(random));
		
		if (buyingItemStack2==ItemStack.EMPTY || amountBought2==0) {
			recipeList.add(new MerchantRecipe(buyingItemStack1, sellingItemstack));
		} else {
			buyingItemStack2.setCount(buying2PriceInfo.getPrice(random));
			recipeList.add(new MerchantRecipe(buyingItemStack1, buyingItemStack2, sellingItemstack));
		}
	}
}
