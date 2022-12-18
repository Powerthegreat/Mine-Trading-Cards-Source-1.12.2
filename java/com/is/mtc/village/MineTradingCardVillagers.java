package com.is.mtc.village;

import javax.annotation.Nullable;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.init.MTCItems;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.util.Reference;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod.EventBusSubscriber
public class MineTradingCardVillagers {
	public static final VillagerProfession PROFESSION_CARD_MASTER = new VillagerProfession(
			Reference.MODID+":card_master",
			Reference.MODID+":textures/entities/card_master.png",
			"minecraft:textures/entity/zombie_villager/zombie_villager.png");
	public static VillagerCareer careerCardMaster;
	
	public static final VillagerProfession PROFESSION_CARD_TRADER = new VillagerProfession(
			Reference.MODID+":card_trader",
			Reference.MODID+":textures/entities/card_trader.png",
			"minecraft:textures/entity/zombie_villager/zombie_villager.png");
	public static VillagerCareer careerCardTrader;
	
	// Indices used to disassemble trade list
	private static final int INDEX_TRADELEVEL = 0;
	private static final int INDEX_TRADECHANCE = 1;
	private static final int INDEX_SELLITEM = 2;
	private static final int INDEX_SELLITEM_AMOUNT = 3;
	private static final int INDEX_BUYITEM1 = 4;
	private static final int INDEX_BUYITEM1_AMOUNT = 5;
	private static final int INDEX_BUYITEM2 = 6;
	private static final int INDEX_BUYITEM2_AMOUNT = 7;
	
	
	public static final String[] CARD_MASTER_TRADE_LIST_DEFAULT = new String[] {
			// Trades that existed in previous versions
//			"1|1.0|common_pack|1-3|emerald|1",
//			"1|1.0|standard_pack|1-2|emerald|1",
//			"1|1.0|edition_pack|1-2|emerald|1",
//			"2|1.0|uncommon_pack|1|emerald|1-2",
//			"3|1.0|rare_pack|1|emerald|3-6",
//			"4|1.0|ancient_pack|1|emerald|5-10",
			
			// Sell either packs for emeralds,
			// or a single pre-generated card for something that may or may not be emeralds.
	        "1|1.0|common_pack|1|emerald|1",
	        "1|1.0|standard_pack|1|emerald|2",
			"1|1.0|common_card_random|1|iron_ingot|2-4",
	        "2|0.9|edition_pack|1|emerald|2|iron_ingot|0-1",
	        "2|1.0|uncommon_pack|1|emerald|2|gold_ingot|0-1",
			"2|0.9|uncommon_card_random|1|gold_ingot|2-3",
	        "3|1.0|rare_pack|1|emerald|3-6",
			"3|0.8|rare_card_random|1|emerald|2-3",
	        "4|1.0|ancient_pack|1|emerald|16-21",
			"4|0.7|ancient_card_random|1|emerald|18-24",
	        "5|0.5|legendary_pack|1|emerald|53-64",
			"5|0.6|legendary_card_random|1|diamond|16-21",
			};
	public static String[] CARD_MASTER_TRADE_LIST = CARD_MASTER_TRADE_LIST_DEFAULT;
	
	public static final String[] CARD_TRADER_TRADE_LIST_DEFAULT = new String[] {
	        // Trade either a specific card for an arbitrary card of that same level,
			// or a non-generated card for two arbitrary cards of that level.
			"1|1.0|common_card_random|1|common_card|1",
	        "1|0.9|common_card|1|common_card|1|common_card|1",
	        "2|1.0|uncommon_card_random|1|uncommon_card|1",
	        "2|0.8|uncommon_card|1|uncommon_card|1|uncommon_card|1",
	        "3|1.0|rare_card_random|1|rare_card|1",
	        "3|0.7|rare_card|1|rare_card|1|rare_card|1",
	        "4|1.0|ancient_card_random|1|ancient_card|1",
	        "4|0.6|ancient_card|1|ancient_card|1|ancient_card|1",
	        "5|0.5|legendary_card_random|1|legendary_card|1",
			};
	public static String[] CARD_TRADER_TRADE_LIST = CARD_TRADER_TRADE_LIST_DEFAULT;
	
	@Nullable
	private static ItemStack getItemStackFromKeyName(String item_key) {
		
		item_key = item_key.toLowerCase().trim();
		
		boolean is_random = item_key.endsWith("_random");
		Item item = null;
		int rarity=-1;
		
		switch (item_key) {
			// Vanilla stuff
			case "iron_ingot":
				item = Items.IRON_INGOT;
				break;
			case "gold_ingot":
				item = Items.GOLD_INGOT;
				break;
			case "emerald":
				item = Items.EMERALD;
				break;
			case "diamond":
				item = Items.DIAMOND;
				break;
			// Packs
			case "common_pack":
				item = MTCItems.packCommon;
				break;
			case "uncommon_pack":
				item = MTCItems.packUncommon;
				break;
			case "rare_pack":
				item = MTCItems.packRare;
				break;
			case "ancient_pack":
				item = MTCItems.packAncient;
				break;
			case "legendary_pack":
				item = MTCItems.packLegendary;
				break;
			case "standard_pack":
				item = MTCItems.packStandard;
				break;
			case "edition_pack":
				item = MTCItems.packEdition;
				break;
			case "custom_pack":
				item = MTCItems.packCustom;
				break;
			// Cards
			case "common_card":
			case "common_card_random":
				item = MTCItems.cardCommon;
				rarity = Rarity.COMMON;
				break;
			case "uncommon_card":
			case "uncommon_card_random":
				item = MTCItems.cardUncommon;
				rarity = Rarity.UNCOMMON;
				break;
			case "rare_card":
			case "rare_card_random":
				item = MTCItems.cardRare;
				rarity = Rarity.RARE;
				break;
			case "ancient_card":
			case "ancient_card_random":
				item = MTCItems.cardAncient;
				rarity = Rarity.ANCIENT;
				break;
			case "legendary_card":
			case "legendary_card_random":
				item = MTCItems.cardLegendary;
				rarity = Rarity.LEGENDARY;
				break;
		}
		
		if (item==null) {
			return ItemStack.EMPTY;
		}
		
		ItemStack returnstack = new ItemStack(item, 1);
		
		// Turn card into specific type
		if (is_random) {
			CardStructure cStruct = Databank.generateACard(rarity);
			if (cStruct != null) {
				returnstack.setTagCompound(new NBTTagCompound());
				returnstack = CardItem.applyCDWDtoStack(returnstack, cStruct);
			}
		}
		
		return returnstack;
	}
	
	private static CardTrade generateTradeFromConfigEntry(String config_entry) {
		
		try {
			String[] split_config_entry = config_entry.toLowerCase().trim().split("\\|");
			
			int trade_level = MathHelper.clamp(Integer.valueOf(split_config_entry[INDEX_TRADELEVEL]), 1, Integer.MAX_VALUE);
			float trade_probability = MathHelper.clamp(Float.valueOf(split_config_entry[INDEX_TRADECHANCE]), 0F, 1F);
			
			boolean use_second_buy_item = split_config_entry.length>INDEX_BUYITEM2;
						
			// Selling item stuff
			String sellitem = split_config_entry[INDEX_SELLITEM];
			String[] sellitem_range = split_config_entry[INDEX_SELLITEM_AMOUNT].trim().split("-");
			int sellamt_low = MathHelper.clamp(Integer.valueOf(sellitem_range[0]), 0, 64);
			int sellamt_high = MathHelper.clamp(Integer.valueOf(sellitem_range[sellitem_range.length>1 ? 1 : 0]), 0, 64);
			ItemStack sellitem_stack = Math.max(sellamt_low, sellamt_high)==0 ? ItemStack.EMPTY : getItemStackFromKeyName(sellitem);
			
			// Buying item (1) stuff
			String buyitem1 = split_config_entry[INDEX_BUYITEM1];
			int buyitem1_random_ind = buyitem1.indexOf("_random");
			
			String[] buyitem1_range = split_config_entry[INDEX_BUYITEM1_AMOUNT].trim().split("-");
			int buyamt1_low = MathHelper.clamp(Integer.valueOf(buyitem1_range[0]), 0, 64);
			int buyamt1_high = MathHelper.clamp(Integer.valueOf(buyitem1_range[buyitem1_range.length>1 ? 1 : 0]), 0, 64);
			ItemStack buyitem1_stack = Math.max(buyamt1_low, buyamt1_high)==0 ? ItemStack.EMPTY : getItemStackFromKeyName(buyitem1);
			
			// Buying item (2) stuff
			ItemStack buyitem2_stack = ItemStack.EMPTY;
			int buyamt2_low = 0;
			int buyamt2_high = 0;
			String buyitem2 = "";
			if (use_second_buy_item) {
				buyitem2 = split_config_entry[INDEX_BUYITEM2];
				int buyitem2_random_ind = buyitem2.indexOf("_random");
				
				String[] buyitem2_range = split_config_entry[INDEX_BUYITEM2_AMOUNT].trim().split("-");
				buyamt2_low = MathHelper.clamp(Integer.valueOf(buyitem2_range[0]), 0, 64);
				buyamt2_high = MathHelper.clamp(Integer.valueOf(buyitem2_range[buyitem2_range.length>1 ? 1 : 0]), 0, 64);
				buyitem2_stack = Math.max(buyamt2_low, buyamt2_high)==0 ? ItemStack.EMPTY : getItemStackFromKeyName(buyitem2);
			}
			
			// Return null if there are malformations
			if (sellitem_stack==ItemStack.EMPTY) {
				Logs.errLog("Skipping registering villager trade " + config_entry + " because sellitem is invalid!");
				return null;
			}
			if (buyitem1_stack==ItemStack.EMPTY) {
				if (buyitem2_stack==ItemStack.EMPTY) {
					Logs.errLog("Skipping registering villager trade " + config_entry + " because both buyitems are invalid and/or rolled a stack size of zero!");
					return null;
				} else {
					Logs.errLog("buyitem1 "+buyitem1+" is invalid in " + config_entry + ", so using " + buyitem2_stack + " instead.");
					return new CardTrade(trade_level, trade_probability,
							buyitem2_stack, new EntityVillager.PriceInfo(buyamt2_low, buyamt2_high),
							sellitem_stack, new EntityVillager.PriceInfo(sellamt_low, sellamt_high)
							);
				}
			}
			
			// Return the proper MerchantRecipe
			if (buyitem2_stack==ItemStack.EMPTY) {
				return new CardTrade(trade_level, trade_probability,
						buyitem1_stack, new EntityVillager.PriceInfo(buyamt1_low, buyamt1_high),
						sellitem_stack, new EntityVillager.PriceInfo(sellamt_low, sellamt_high)
						);
			} else {
				return new CardTrade(trade_level, trade_probability,
						buyitem1_stack, new EntityVillager.PriceInfo(buyamt1_low, buyamt1_high),
						buyitem2_stack, new EntityVillager.PriceInfo(buyamt2_low, buyamt2_high),
						sellitem_stack, new EntityVillager.PriceInfo(sellamt_low, sellamt_high)
						);
			}
			
		} catch (Exception e) {
			Logs.errLog("Skipping villager trade " + config_entry + " because something went wrong! Check your formatting.");
			return null;
		}
	}
	
	@SubscribeEvent
	public static void registerVillagers(RegistryEvent.Register<VillagerProfession> event) {
		event.getRegistry().registerAll(PROFESSION_CARD_MASTER);
		event.getRegistry().registerAll(PROFESSION_CARD_TRADER);
	}

	public static void registerCareers() {
		Logs.stdLog("Registering villager careers");
		
		// Card Master
		careerCardMaster = (new VillagerCareer(PROFESSION_CARD_MASTER, "card_master"));
		
		// Iterate through config entries and add them as trades
		for (int i=0; i<CARD_MASTER_TRADE_LIST.length; i++) {
			
			CardTrade card_villager_trade = generateTradeFromConfigEntry(CARD_MASTER_TRADE_LIST[i]);
			
			if (card_villager_trade!=null) {
				careerCardMaster.addTrade(card_villager_trade.getTradeLevel(), card_villager_trade);
			}
		}
		

		// Card Trader
		careerCardTrader = (new VillagerCareer(PROFESSION_CARD_TRADER, "card_trader"));
		
		// Iterate through config entries and add them as trades
		for (int i=0; i<CARD_TRADER_TRADE_LIST.length; i++) {
			
			CardTrade card_villager_trade = generateTradeFromConfigEntry(CARD_TRADER_TRADE_LIST[i]);
			
			if (card_villager_trade!=null) {
				careerCardTrader.addTrade(card_villager_trade.getTradeLevel(), card_villager_trade);
			}
		}
	}
}
