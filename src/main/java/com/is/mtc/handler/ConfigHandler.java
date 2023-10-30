package com.is.mtc.handler;

import com.is.mtc.MineTradingCards;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Functions;
import com.is.mtc.util.Reference;
import com.is.mtc.village.CardMasterHomeHandler;
import com.is.mtc.village.MineTradingCardVillagers;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	public static final String CONFIG_CAT_COLORS = "colors";
	public static final String CONFIG_CAT_DROPS = "drops";
	public static final String CONFIG_CAT_LOGS = "logs";
	public static final String CONFIG_CAT_PACK_CONTENTS = "pack contents";
	public static final String CONFIG_CAT_RECIPES = "recipes";
	public static final String CONFIG_CAT_UPDATES = "updates";
	public static final String CONFIG_CAT_VILLAGERS = "villagers";
	// Config text
	public static final String COLOR_ITEM_DESCRIPTION_1 = "Color for ";
	public static final String COLOR_ITEM_DESCRIPTION_2 = "Entered as a decimal integer, or as a hexadecimal by putting # in front.";
	public static final String COLOR_TOOLTIP_1 = "Tooltip color for ";
	public static final String COLOR_TOOLTIP_2 = " cards, using \"friendly\" Minecraft color name";
	// Configuration categories
	public static Configuration config;

	public static void saveConfig() {
		// === Colors ===
		// Cards
		MineTradingCards.CARD_COLOR_COMMON = Functions.parseColorInteger(config.getString("card_color_common", CONFIG_CAT_COLORS, "#55ff55", COLOR_ITEM_DESCRIPTION_1 + "common cards. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GREEN);
		MineTradingCards.CARD_COLOR_UNCOMMON = Functions.parseColorInteger(config.getString("card_color_uncommon", CONFIG_CAT_COLORS, "#ffaa00", COLOR_ITEM_DESCRIPTION_1 + "uncommon cards. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GOLD);
		MineTradingCards.CARD_COLOR_RARE = Functions.parseColorInteger(config.getString("card_color_rare", CONFIG_CAT_COLORS, "#ff5555", COLOR_ITEM_DESCRIPTION_1 + "rare cards. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_RED);
		MineTradingCards.CARD_COLOR_ANCIENT = Functions.parseColorInteger(config.getString("card_color_ancient", CONFIG_CAT_COLORS, "#55ffff", COLOR_ITEM_DESCRIPTION_1 + "ancient cards. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_AQUA);
		MineTradingCards.CARD_COLOR_LEGENDARY = Functions.parseColorInteger(config.getString("card_color_legendary", CONFIG_CAT_COLORS, "#ff55ff", COLOR_ITEM_DESCRIPTION_1 + "legendary cards. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_LIGHT_PURPLE);
		// Tooltips
		MineTradingCards.CARD_TOOLTIP_COLOR_COMMON = config.getString("card_tooltip_color_common", CONFIG_CAT_COLORS, "green", COLOR_TOOLTIP_1 + "common" + COLOR_TOOLTIP_2);
		MineTradingCards.CARD_TOOLTIP_COLOR_UNCOMMON = config.getString("card_tooltip_color_uncommon", CONFIG_CAT_COLORS, "gold", COLOR_TOOLTIP_1 + "uncommon" + COLOR_TOOLTIP_2);
		MineTradingCards.CARD_TOOLTIP_COLOR_RARE = config.getString("card_tooltip_color_rare", CONFIG_CAT_COLORS, "red", COLOR_TOOLTIP_1 + "rare" + COLOR_TOOLTIP_2);
		MineTradingCards.CARD_TOOLTIP_COLOR_ANCIENT = config.getString("card_tooltip_color_ancient", CONFIG_CAT_COLORS, "aqua", COLOR_TOOLTIP_1 + "ancient" + COLOR_TOOLTIP_2);
		MineTradingCards.CARD_TOOLTIP_COLOR_LEGENDARY = config.getString("card_tooltip_color_legendary", CONFIG_CAT_COLORS, "light_purple", COLOR_TOOLTIP_1 + "legendary" + COLOR_TOOLTIP_2);
		// Packs
		MineTradingCards.PACK_COLOR_COMMON = Functions.parseColorInteger(config.getString("pack_color_common", CONFIG_CAT_COLORS, "#55ff55", COLOR_ITEM_DESCRIPTION_1 + "common packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GREEN);
		MineTradingCards.PACK_COLOR_UNCOMMON = Functions.parseColorInteger(config.getString("pack_color_uncommon", CONFIG_CAT_COLORS, "#ffaa00", COLOR_ITEM_DESCRIPTION_1 + "uncommon packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GOLD);
		MineTradingCards.PACK_COLOR_RARE = Functions.parseColorInteger(config.getString("pack_color_rare", CONFIG_CAT_COLORS, "#ff5555", COLOR_ITEM_DESCRIPTION_1 + "rare packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_RED);
		MineTradingCards.PACK_COLOR_ANCIENT = Functions.parseColorInteger(config.getString("pack_color_ancient", CONFIG_CAT_COLORS, "#55ffff", COLOR_ITEM_DESCRIPTION_1 + "ancient packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_AQUA);
		MineTradingCards.PACK_COLOR_LEGENDARY = Functions.parseColorInteger(config.getString("pack_color_legendary", CONFIG_CAT_COLORS, "#ff55ff", COLOR_ITEM_DESCRIPTION_1 + "legendary packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_LIGHT_PURPLE);
		MineTradingCards.PACK_COLOR_STANDARD = Functions.parseColorInteger(config.getString("pack_color_standard", CONFIG_CAT_COLORS, "#5555ff", COLOR_ITEM_DESCRIPTION_1 + "standard packs. " + COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_BLUE);

		// === Drops ===
		// Drops toggle
		DropHandler.CAN_DROP_CARDS_MOB = config.getBoolean("mobs_can_drop_cards", CONFIG_CAT_DROPS, true, "Mobs will drop cards on death.");
		DropHandler.CAN_DROP_CARDS_ANIMAL = config.getBoolean("animals_can_drop_cards", CONFIG_CAT_DROPS, false, "Animals will drop cards on death.");
		DropHandler.CAN_DROP_CARDS_PLAYER = config.getBoolean("players_can_drop_cards", CONFIG_CAT_DROPS, false, "Players will drop cards on death.");
		DropHandler.CAN_DROP_PACKS_MOB = config.getBoolean("mobs_can_drop_packs", CONFIG_CAT_DROPS, true, "Mobs will drop packs on death.");
		DropHandler.CAN_DROP_PACKS_ANIMAL = config.getBoolean("animals_can_drop_packs", CONFIG_CAT_DROPS, false, "Animals will drop packs on death.");
		DropHandler.CAN_DROP_PACKS_PLAYER = config.getBoolean("players_can_drop_packs", CONFIG_CAT_DROPS, false, "Players will drop packs on death.");
		// Tiered card drop rates
		DropHandler.CARD_DROP_RATE_COM = config.getFloat("card_drop_rate_common", CONFIG_CAT_DROPS, 0, 0, Float.MAX_VALUE, "Chance out of X to drop common cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_UNC = config.getFloat("card_drop_rate_uncommon", CONFIG_CAT_DROPS, 0, 0, Float.MAX_VALUE, "Chance out of X to drop uncommon cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_RAR = config.getFloat("card_drop_rate_rare", CONFIG_CAT_DROPS, 0, 0, Float.MAX_VALUE, "Chance out of X to drop rare cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_ANC = config.getFloat("card_drop_rate_ancient", CONFIG_CAT_DROPS, 0, 0, Float.MAX_VALUE, "Chance out of X to drop ancient cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_LEG = config.getFloat("card_drop_rate_legendary", CONFIG_CAT_DROPS, 0, 0, Float.MAX_VALUE, "Chance out of X to drop legendary cards. Set to 0 to disable.");
		// Tiered pack drop rates
		DropHandler.PACK_DROP_RATE_COM = config.getFloat("pack_drop_rate_common", CONFIG_CAT_DROPS, 16, 0, Float.MAX_VALUE, "Chance out of X to drop common packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_UNC = config.getFloat("pack_drop_rate_uncommon", CONFIG_CAT_DROPS, 32, 0, Float.MAX_VALUE, "Chance out of X to drop uncommon packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_RAR = config.getFloat("pack_drop_rate_rare", CONFIG_CAT_DROPS, 48, 0, Float.MAX_VALUE, "Chance out of X to drop rare packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_ANC = config.getFloat("pack_drop_rate_ancient", CONFIG_CAT_DROPS, 64, 0, Float.MAX_VALUE, "Chance out of X to drop ancient packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_LEG = config.getFloat("pack_drop_rate_legendary", CONFIG_CAT_DROPS, 256, 0, Float.MAX_VALUE, "Chance out of X to drop legendary packs. Set to 0 to disable.");
		// Non-tiered pack drop rates
		DropHandler.PACK_DROP_RATE_STD = config.getFloat("pack_drop_rate_standard", CONFIG_CAT_DROPS, 40, 0, Float.MAX_VALUE, "Chance out of X to drop standard packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_EDT = config.getFloat("pack_drop_rate_edition", CONFIG_CAT_DROPS, 40, 0, Float.MAX_VALUE, "Chance out of X to drop set-specific (edition) packs. Set to 0 to disable.");
		DropHandler.PACK_DROP_RATE_CUS = config.getFloat("pack_drop_rate_custom", CONFIG_CAT_DROPS, 40, 0, Float.MAX_VALUE, "Chance out of X to drop custom packs. Set to 0 to disable.");
		// Other
		DropHandler.ONLY_ONE_DROP = config.getBoolean("only_one_drop", CONFIG_CAT_DROPS, false, "If true, entities will not drop more than one MTC item at once.");
		// Boss drops
		DropHandler.ENDER_DRAGON_DROPS = config.getStringList("ender_dragon_drops", CONFIG_CAT_DROPS, DropHandler.ENDER_DRAGON_DROPS_DEFAULT,
				"List of MTC drops from the Ender Dragon. Entries are of the form:"
						+ "\ndrop_item:float_amount"
						+ "\nPossible drop_item values are:"
						+ "\n[common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
						+ "\nfloat_amount is an either an integer representing the number of this item to drop, or a float like 4.7, where e.g. 70%, 5 will be dropped; otherwise 4 will."
						+ "\nThis list applies even if \"can_drop\" is false."
		);
		// Boss drops
		DropHandler.BOSS_DROPS = config.getStringList("boss_drops", CONFIG_CAT_DROPS, DropHandler.BOSS_DROPS_DEFAULT,
				"List of MTC drops from the bosses that aren't the Ender Dragon. Entries are of the form:"
						+ "\ndrop_item:float_amount"
						+ "\nPossible drop_item values are:"
						+ "\n[common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
						+ "\nfloat_amount is an either an integer representing the number of this item to drop, or a float like 4.7, where e.g. 70%, 5 will be dropped; otherwise 4 will."
						+ "\nThis list applies even if \"can_drop\" is false."
		);


		// === Pack contents ===
		PackItemRarity.COMMON_PACK_CONTENT = config.getStringList("common_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemRarity.COMMON_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Common", true));
		PackItemRarity.UNCOMMON_PACK_CONTENT = config.getStringList("uncommon_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemRarity.UNCOMMON_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Uncommon", true));
		PackItemRarity.RARE_PACK_CONTENT = config.getStringList("rare_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemRarity.RARE_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Rare", true));
		PackItemRarity.ANCIENT_PACK_CONTENT = config.getStringList("ancient_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemRarity.ANCIENT_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Ancient", true));
		PackItemRarity.LEGENDARY_PACK_CONTENT = config.getStringList("legendary_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemRarity.LEGENDARY_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Legendary", true));
		PackItemStandard.STANDARD_PACK_CONTENT = config.getStringList("standard_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Standard", false));
		PackItemEdition.EDITION_PACK_CONTENT = config.getStringList("edition_pack_contents", CONFIG_CAT_PACK_CONTENTS, PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT, cardPackDistributionDescription("Edition", false));


		// === Villager ===
		MineTradingCardVillagers.CARD_MASTER_TRADE_LIST = config.getStringList("card_master_trades", CONFIG_CAT_VILLAGERS, MineTradingCardVillagers.CARD_MASTER_TRADE_LIST_DEFAULT,
				"List of possible Card Master trades. Entries are of the form:"
						+ "\nsellitem|amount|buyitem1|amount|buyitem2|amount"
						+ "\n\"amount\" is either an integer, or a range like 1-3."
						+ "\nbuyitem2|amount is optional."
						+ "\nPossible sellitem and buyitem values are:"
						+ "\niron_ingot, gold_ingot, emerald, diamond, [common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
						+ "\nYou also append \"_random\" at the end of a _card entry (e.g. common_card_random) in order to generate a random card for sale (or requested)."
		);
		MineTradingCardVillagers.CARD_TRADER_TRADE_LIST = config.getStringList("card_trader_trades", CONFIG_CAT_VILLAGERS, MineTradingCardVillagers.CARD_TRADER_TRADE_LIST_DEFAULT,
				"List of possible Card Trader trades. Entries are of the form:"
						+ "\nsellitem|amount|buyitem1|amount|buyitem2|amount"
						+ "\n\"amount\" is either an integer, or a range like 1-3."
						+ "\nbuyitem2|amount is optional."
						+ "\nPossible sellitem and buyitem values are:"
						+ "\niron_ingot, gold_ingot, emerald, diamond, [common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
						+ "\nYou also append \"_random\" at the end of a _card entry (e.g. common_card_random) in order to generate a random card for sale (or requested)."
		);
		CardMasterHomeHandler.SHOP_WEIGHT = config.getInt("card_shop_weight", CONFIG_CAT_VILLAGERS, 5, 0, 100, "Weighting for selection when villages generate. Farms and wood huts are 3, church is 20.");
		CardMasterHomeHandler.SHOP_MAX_NUMBER = config.getInt("card_shop_max_number", CONFIG_CAT_VILLAGERS, 1, 0, 32, "Maximum number of card master shops that can spawn per village");

		// === Logging ===
		Logs.ENABLE_DEV_LOGS = config.getBoolean("devlog_enabled", CONFIG_CAT_LOGS, false, "Enable developer logs");

		// === Update Checker ===
		MineTradingCards.ENABLE_UPDATE_CHECKER = config.getBoolean("enable_update_checker", CONFIG_CAT_UPDATES, true, "Displays a client-side chat message on login if there's an update available.");


		if (config.hasChanged()) config.save();
	}

	private static final String cardPackDistributionDescription(String rarity, boolean allowNx) {
		return "Number and type of cards dropped when a " + rarity + " pack is opened. Entries are of the form:"
				+ "\nNxW:W:W:W:W" + (allowNx ? " or Nx" : "")
				+ "\nN is the number of cards added from this row. It can be an integer (e.g. 5) or it can be a float like 3.4 (40% of the time 4 will be generated; otherwise 3 will)."
				+ "\nx is just the letter x. Leave this as is."
				+ "\nW:W:W:W:W:W is a distribution of rarity weights representing Com:Unc:Rar:Anc:Leg. Each card generated from this row will be drawn from this distribution. "
				+ "For example: 0:1:0:0:1 has an equal chance of being an Uncommon or Legendary card. 2:1:1:1:1 can be any rarity, but is twice as likely to be " + rarity + " as the other rarities."
				+ (allowNx ? "\nFor Nx formatting, the weighting portion is omitted. All cards genrerated are " + rarity + ", because this is a " + rarity + " pack. N can be an integer or a float, as explained above." : "");
	}
}
