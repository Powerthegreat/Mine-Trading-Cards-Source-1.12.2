package com.is.mtc.village;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import javax.annotation.Nullable;
import java.util.Random;

public class CardTrade implements EntityVillager.ITradeList {
	private int tradeLevel;
	private float tradeChance;
	private ItemStack buyingItemStack1;
	private boolean buyingItemStack1IsRandom;
	private EntityVillager.PriceInfo buying1PriceInfo;
	private ItemStack buyingItemStack2;
	private boolean buyingItemStack2IsRandom;
	private EntityVillager.PriceInfo buying2PriceInfo;
	private ItemStack sellingItemStack;
	private boolean sellingItemStackIsRandom;
	private EntityVillager.PriceInfo sellingPriceInfo;

	public CardTrade() {
	}

	@Nullable
	public CardTrade(int tradelevel, float tradechance,
					 ItemStack buyitem1_stack, boolean buy1IsRandom, EntityVillager.PriceInfo buy1Number,
					 ItemStack buyitem2_stack, boolean buy2IsRandom, EntityVillager.PriceInfo buy2Number,
					 ItemStack sellitem_stack, boolean sellIsRandom, EntityVillager.PriceInfo sellNumber) {
		tradeLevel = tradelevel;
		tradeChance = tradechance;
		buyingItemStack1 = buyitem1_stack;
		buyingItemStack1IsRandom = buy1IsRandom;
		buying1PriceInfo = buy1Number;
		buyingItemStack2 = buyitem2_stack;
		buyingItemStack2IsRandom = buy2IsRandom;
		buying2PriceInfo = buy2Number;
		sellingItemStack = sellitem_stack;
		sellingItemStackIsRandom = sellIsRandom;
		sellingPriceInfo = sellNumber;
	}

	@Nullable
	public CardTrade(int tradelevel, float tradechance,
					 ItemStack buyitem1_stack, boolean buy1IsRandom, EntityVillager.PriceInfo buy1Number,
					 ItemStack sellitem_stack, boolean sellIsRandom, EntityVillager.PriceInfo sellNumber) {
		tradeLevel = tradelevel;
		tradeChance = tradechance;
		buyingItemStack1 = buyitem1_stack;
		buyingItemStack1IsRandom = buy1IsRandom;
		buying1PriceInfo = buy1Number;
		buyingItemStack2 = ItemStack.EMPTY;
		buyingItemStack2IsRandom = false;
		buying2PriceInfo = new EntityVillager.PriceInfo(0, 0);
		sellingItemStack = sellitem_stack;
		sellingItemStackIsRandom = sellIsRandom;
		sellingPriceInfo = sellNumber;
	}

	public int getTradeLevel() {
		return tradeLevel;
	}

	private ItemStack randomizeMerchantCard(ItemStack stack, Random random) {
		if (stack == null || stack.getCount() == 0 || !(stack.getItem() instanceof CardItem)) {
			return stack;
		}

		// If this is a valid card type, generate a random one
		CardStructure cStruct = Databank.generateACard(((CardItem) stack.getItem()).getCardRarity(), random);
		if (cStruct != null) {
			stack.setTagCompound(new NBTTagCompound());
			stack = CardItem.applyCDWDtoStack(stack, cStruct, random);
		}
		return stack;
	}

	@Override
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
		int amountBought1 = buying1PriceInfo.getPrice(random);
		int amountBought2 = buying2PriceInfo.getPrice(random);
		int amountSold = sellingPriceInfo.getPrice(random);

		// Don't add a trade if it's invalid
		if (buyingItemStack1 == ItemStack.EMPTY || amountBought1 == 0
				|| sellingItemStack == ItemStack.EMPTY || amountSold == 0) {
			return;
		}

		// Don't add a trade probabilistically
		if (random.nextFloat() >= tradeChance) {
			return;
		}

		// Randomize cards if asked to!
		if (sellingItemStackIsRandom) {
			sellingItemStack = randomizeMerchantCard(sellingItemStack, random);
		}
		if (buyingItemStack1IsRandom) {
			buyingItemStack1 = randomizeMerchantCard(buyingItemStack1, random);
		}
		if (buyingItemStack2IsRandom) {
			buyingItemStack2 = randomizeMerchantCard(buyingItemStack2, random);
		}

		// Adjust itemstacks with amounts provided
		buyingItemStack1.setCount(buying1PriceInfo.getPrice(random));
		sellingItemStack.setCount(sellingPriceInfo.getPrice(random));

		if (buyingItemStack2 == ItemStack.EMPTY || amountBought2 == 0) {
			recipeList.add(new MerchantRecipe(buyingItemStack1, sellingItemStack));
		} else {
			buyingItemStack2.setCount(buying2PriceInfo.getPrice(random));
			recipeList.add(new MerchantRecipe(buyingItemStack1, buyingItemStack2, sellingItemStack));
		}
	}
}
