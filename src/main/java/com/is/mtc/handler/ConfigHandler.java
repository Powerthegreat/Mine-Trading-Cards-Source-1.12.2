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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigHandler {
	public static final MineTradingCardsConfig CONFIG;
	public static final ForgeConfigSpec CONFIG_SPEC;
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

	static {
		final Pair<MineTradingCardsConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MineTradingCardsConfig::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
		//final CommentedFileConfig file = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get() + "/MineTradingCards.toml")
		//.sync()
		//.autosave()
		//.writingMode(WritingMode.REPLACE)
		//.build();
		//file.load();
		//CONFIG_SPEC.setConfig(file);
	}

	public static void bakeConfig() {
		// === Colors ===
		// Cards
		MineTradingCards.CARD_COLOR_COMMON = Functions.parseColorInteger(CONFIG.cardColorCommon.get(), Reference.COLOR_GREEN);
		MineTradingCards.CARD_COLOR_UNCOMMON = Functions.parseColorInteger(CONFIG.cardColorUncommon.get(), Reference.COLOR_GOLD);
		MineTradingCards.CARD_COLOR_RARE = Functions.parseColorInteger(CONFIG.cardColorRare.get(), Reference.COLOR_RED);
		MineTradingCards.CARD_COLOR_ANCIENT = Functions.parseColorInteger(CONFIG.cardColorAncient.get(), Reference.COLOR_AQUA);
		MineTradingCards.CARD_COLOR_LEGENDARY = Functions.parseColorInteger(CONFIG.cardColorLegendary.get(), Reference.COLOR_LIGHT_PURPLE);
		// Tooltips
		MineTradingCards.CARD_TOOLTIP_COLOR_COMMON = CONFIG.cardTooltipColorCommon.get();
		MineTradingCards.CARD_TOOLTIP_COLOR_UNCOMMON = CONFIG.cardTooltipColorUncommon.get();
		MineTradingCards.CARD_TOOLTIP_COLOR_RARE = CONFIG.cardTooltipColorRare.get();
		MineTradingCards.CARD_TOOLTIP_COLOR_ANCIENT = CONFIG.cardTooltipColorAncient.get();
		MineTradingCards.CARD_TOOLTIP_COLOR_LEGENDARY = CONFIG.cardTooltipColorLegendary.get();
		// Packs
		MineTradingCards.PACK_COLOR_COMMON = Functions.parseColorInteger("#55ff55", Reference.COLOR_GREEN);
		MineTradingCards.PACK_COLOR_UNCOMMON = Functions.parseColorInteger("#ffaa00", Reference.COLOR_GOLD);
		MineTradingCards.PACK_COLOR_RARE = Functions.parseColorInteger("#ff5555", Reference.COLOR_RED);
		MineTradingCards.PACK_COLOR_ANCIENT = Functions.parseColorInteger("#55ffff", Reference.COLOR_AQUA);
		MineTradingCards.PACK_COLOR_LEGENDARY = Functions.parseColorInteger("#ff55ff", Reference.COLOR_LIGHT_PURPLE);
		MineTradingCards.PACK_COLOR_STANDARD = Functions.parseColorInteger("#5555ff", Reference.COLOR_BLUE);

		// === Drops ===
		// Drops toggle
		DropHandler.CAN_DROP_CARDS_MOB = CONFIG.mobsCanDropCards.get();
		DropHandler.CAN_DROP_CARDS_ANIMAL = CONFIG.animalsCanDropCards.get();
		DropHandler.CAN_DROP_CARDS_PLAYER = CONFIG.playersCanDropCards.get();
		DropHandler.CAN_DROP_PACKS_MOB = CONFIG.mobsCanDropPacks.get();
		DropHandler.CAN_DROP_PACKS_ANIMAL = CONFIG.animalsCanDropPacks.get();
		DropHandler.CAN_DROP_PACKS_PLAYER = CONFIG.playersCanDropPacks.get();
		// Tiered card drop rates
		DropHandler.CARD_DROP_RATE_COM = CONFIG.cardDropRateCommon.get();
		DropHandler.CARD_DROP_RATE_UNC = CONFIG.cardDropRateUncommon.get();
		DropHandler.CARD_DROP_RATE_RAR = CONFIG.cardDropRateRare.get();
		DropHandler.CARD_DROP_RATE_ANC = CONFIG.cardDropRateAncient.get();
		DropHandler.CARD_DROP_RATE_LEG = CONFIG.cardDropRateLegendary.get();
		// Tiered pack drop rates
		DropHandler.PACK_DROP_RATE_COM = CONFIG.packDropRateCommon.get();
		DropHandler.PACK_DROP_RATE_UNC = CONFIG.packDropRateUncommon.get();
		DropHandler.PACK_DROP_RATE_RAR = CONFIG.packDropRateRare.get();
		DropHandler.PACK_DROP_RATE_ANC = CONFIG.packDropRateAncient.get();
		DropHandler.PACK_DROP_RATE_LEG = CONFIG.packDropRateLegendary.get();
		// Non-tiered pack drop rates
		DropHandler.PACK_DROP_RATE_STD = CONFIG.packDropRateStandard.get();
		DropHandler.PACK_DROP_RATE_EDT = CONFIG.packDropRateEdition.get();
		DropHandler.PACK_DROP_RATE_CUS = CONFIG.packDropRateCustom.get();
		// Other
		DropHandler.ONLY_ONE_DROP = CONFIG.onlyOneDrop.get();
		// Boss drops
		DropHandler.ENDER_DRAGON_DROPS = CONFIG.enderDragonDrops.get();
		DropHandler.BOSS_DROPS = CONFIG.bossDrops.get();

		// === Pack contents ===
		PackItemRarity.COMMON_PACK_CONTENT = CONFIG.commonPackContents.get();
		PackItemRarity.UNCOMMON_PACK_CONTENT = CONFIG.uncommonPackContents.get();
		PackItemRarity.RARE_PACK_CONTENT = CONFIG.rarePackContents.get();
		PackItemRarity.ANCIENT_PACK_CONTENT = CONFIG.ancientPackContents.get();
		PackItemRarity.LEGENDARY_PACK_CONTENT = CONFIG.legendaryPackContents.get();
		PackItemStandard.STANDARD_PACK_CONTENT = CONFIG.standardPackContents.get();
		PackItemEdition.EDITION_PACK_CONTENT = CONFIG.editionPackContents.get();

		// === Villager ===
		MineTradingCardVillagers.CARD_MASTER_TRADE_LIST = CONFIG.cardMasterTrades.get();
		MineTradingCardVillagers.CARD_TRADER_TRADE_LIST = CONFIG.cardTraderTrades.get();
		CardMasterHomeHandler.SHOP_WEIGHT = CONFIG.cardShopWeight.get();
		CardMasterHomeHandler.SHOP_MAX_NUMBER = CONFIG.cardShopMaxNumber.get();

		// === Logging ===
		Logs.ENABLE_DEV_LOGS = CONFIG.devlogEnabled.get();

		// === Update Checker ===
		MineTradingCards.ENABLE_UPDATE_CHECKER = CONFIG.enableUpdateChecker.get();
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == CONFIG_SPEC) {
			bakeConfig();
		}
	}

	private static String cardPackDistributionDescription(String rarity, boolean allowNx) {
		return "Number and type of cards dropped when a " + rarity + " pack is opened. Entries are of the form:"
				+ "\nNxW:W:W:W:W" + (allowNx ? " or Nx" : "")
				+ "\nN is the number of cards added from this row. It can be an integer (e.g. 5) or it can be a float like 3.4 (40% of the time 4 will be generated; otherwise 3 will)."
				+ "\nx is just the letter x. Leave this as is."
				+ "\nW:W:W:W:W:W is a distribution of rarity weights representing Com:Unc:Rar:Anc:Leg. Each card generated from this row will be drawn from this distribution. "
				+ "For example: 0:1:0:0:1 has an equal chance of being an Uncommon or Legendary card. 2:1:1:1:1 can be any rarity, but is twice as likely to be " + rarity + " as the other rarities."
				+ (allowNx ? "\nFor Nx formatting, the weighting portion is omitted. All cards genrerated are " + rarity + ", because this is a " + rarity + " pack. N can be an integer or a float, as explained above." : "");
	}

	public static class MineTradingCardsConfig {
		// === Colors ===
		// Cards
		public final ForgeConfigSpec.ConfigValue<String> cardColorCommon;
		public final ForgeConfigSpec.ConfigValue<String> cardColorUncommon;
		public final ForgeConfigSpec.ConfigValue<String> cardColorRare;
		public final ForgeConfigSpec.ConfigValue<String> cardColorAncient;
		public final ForgeConfigSpec.ConfigValue<String> cardColorLegendary;
		// Tooltips
		public final ForgeConfigSpec.ConfigValue<String> cardTooltipColorCommon;
		public final ForgeConfigSpec.ConfigValue<String> cardTooltipColorUncommon;
		public final ForgeConfigSpec.ConfigValue<String> cardTooltipColorRare;
		public final ForgeConfigSpec.ConfigValue<String> cardTooltipColorAncient;
		public final ForgeConfigSpec.ConfigValue<String> cardTooltipColorLegendary;
		// Packs
		public final ForgeConfigSpec.ConfigValue<String> packColorCommon;
		public final ForgeConfigSpec.ConfigValue<String> packColorUncommon;
		public final ForgeConfigSpec.ConfigValue<String> packColorRare;
		public final ForgeConfigSpec.ConfigValue<String> packColorAncient;
		public final ForgeConfigSpec.ConfigValue<String> packColorLegendary;
		public final ForgeConfigSpec.ConfigValue<String> packColorStandard;

		// === Drops ===
		// Drops toggle
		public final ForgeConfigSpec.BooleanValue mobsCanDropCards;
		public final ForgeConfigSpec.BooleanValue animalsCanDropCards;
		public final ForgeConfigSpec.BooleanValue playersCanDropCards;
		public final ForgeConfigSpec.BooleanValue mobsCanDropPacks;
		public final ForgeConfigSpec.BooleanValue animalsCanDropPacks;
		public final ForgeConfigSpec.BooleanValue playersCanDropPacks;
		// Tiered card drop rates
		public final ForgeConfigSpec.DoubleValue cardDropRateCommon;
		public final ForgeConfigSpec.DoubleValue cardDropRateUncommon;
		public final ForgeConfigSpec.DoubleValue cardDropRateRare;
		public final ForgeConfigSpec.DoubleValue cardDropRateAncient;
		public final ForgeConfigSpec.DoubleValue cardDropRateLegendary;
		// Tiered pack drop rates
		public final ForgeConfigSpec.DoubleValue packDropRateCommon;
		public final ForgeConfigSpec.DoubleValue packDropRateUncommon;
		public final ForgeConfigSpec.DoubleValue packDropRateRare;
		public final ForgeConfigSpec.DoubleValue packDropRateAncient;
		public final ForgeConfigSpec.DoubleValue packDropRateLegendary;
		// Non-tiered pack drop rates
		public final ForgeConfigSpec.DoubleValue packDropRateStandard;
		public final ForgeConfigSpec.DoubleValue packDropRateEdition;
		public final ForgeConfigSpec.DoubleValue packDropRateCustom;
		// Other
		public final ForgeConfigSpec.BooleanValue onlyOneDrop;
		// Boss drops
		public final ForgeConfigSpec.ConfigValue<List<String>> enderDragonDrops;
		public final ForgeConfigSpec.ConfigValue<List<String>> bossDrops;

		// === Pack contents ===
		public final ForgeConfigSpec.ConfigValue<List<String>> commonPackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> uncommonPackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> rarePackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> ancientPackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> legendaryPackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> standardPackContents;
		public final ForgeConfigSpec.ConfigValue<List<String>> editionPackContents;

		// === Villager ===
		public final ForgeConfigSpec.ConfigValue<List<String>> cardMasterTrades;
		public final ForgeConfigSpec.ConfigValue<List<String>> cardTraderTrades;
		public final ForgeConfigSpec.IntValue cardShopWeight;
		public final ForgeConfigSpec.IntValue cardShopMaxNumber;

		// === Logging ===
		public final ForgeConfigSpec.BooleanValue devlogEnabled;

		// === Update Checker ===
		public final ForgeConfigSpec.BooleanValue enableUpdateChecker;

		public MineTradingCardsConfig(ForgeConfigSpec.Builder builder) {
			// === Colors ===
			// Cards
			builder.push(CONFIG_CAT_COLORS);
			cardColorCommon = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "common cards. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".cardColorCommon")
					.define("card_color_common", "#55ff55");
			cardColorUncommon = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "uncommon cards. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".cardColorUncommon")
					.define("card_Color_Uncommon", "#ffaa00");
			cardColorRare = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "rare cards. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".cardColorRare")
					.define("card_Color_Rare", "#ff5555");
			cardColorAncient = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "ancient cards. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".cardColorAncient")
					.define("card_Color_Ancient", "#55ffff");
			cardColorLegendary = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "legendary cards. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".cardColorLegendary")
					.define("card_Color_Legendary", "#ff55ff");
			// Tooltips
			cardTooltipColorCommon = builder
					.comment(COLOR_TOOLTIP_1 + "common" + COLOR_TOOLTIP_2)
					.translation("config." + Reference.MODID + ".cardTooltipColorCommon")
					.define("card_tooltip_color_common", "green");
			cardTooltipColorUncommon = builder
					.comment(COLOR_TOOLTIP_1 + "uncommon" + COLOR_TOOLTIP_2)
					.translation("config." + Reference.MODID + ".cardTooltipColorUncommon")
					.define("card_tooltip_color_uncommon", "gold");
			cardTooltipColorRare = builder
					.comment(COLOR_TOOLTIP_1 + "rare" + COLOR_TOOLTIP_2)
					.translation("config." + Reference.MODID + ".cardTooltipColorRare")
					.define("card_tooltip_color_rare", "red");
			cardTooltipColorAncient = builder
					.comment(COLOR_TOOLTIP_1 + "ancient" + COLOR_TOOLTIP_2)
					.translation("config." + Reference.MODID + ".cardTooltipColorAncient")
					.define("card_tooltip_color_ancient", "aqua");
			cardTooltipColorLegendary = builder
					.comment(COLOR_TOOLTIP_1 + "legendary" + COLOR_TOOLTIP_2)
					.translation("config." + Reference.MODID + ".cardTooltipColorLegendary")
					.define("card_tooltip_color_legendary", "light_purple");
			// Packs
			packColorCommon = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "common packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorCommon")
					.define("pack_color_common", "#55ff55");
			packColorUncommon = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "uncommon packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorUncommon")
					.define("pack_color_uncommon", "#ffaa00");
			packColorRare = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "rare packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorRare")
					.define("pack_color_rare", "#ff5555");
			packColorAncient = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "ancient packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorAncient")
					.define("pack_color_ancient", "#55ffff");
			packColorLegendary = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "legendary packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorLegendary")
					.define("pack_color_legendary", "#ff55ff");
			packColorStandard = builder
					.comment(COLOR_ITEM_DESCRIPTION_1 + "standard packs. " + COLOR_ITEM_DESCRIPTION_2.trim())
					.translation("config." + Reference.MODID + ".packColorStandard")
					.define("pack_color_standard", "#5555ff");
			builder.pop();

			// === Drops ===
			// Drops toggle
			builder.push(CONFIG_CAT_DROPS);
			mobsCanDropCards = builder
					.comment("Mobs will drop cards on death.")
					.translation("config." + Reference.MODID + ".mobsCanDropCards")
					.define("mobs_can_drop_cards", true);
			animalsCanDropCards = builder
					.comment("Animals will drop cards on death.")
					.translation("config." + Reference.MODID + ".animalsCanDropCards")
					.define("animals_can_drop_cards", false);
			playersCanDropCards = builder
					.comment("Players will drop cards on death.")
					.translation("config." + Reference.MODID + ".playersCanDropCards")
					.define("players_can_drop_cards", false);
			mobsCanDropPacks = builder
					.comment("Mobs will drop packs on death.")
					.translation("config." + Reference.MODID + ".mobsCanDropPacks")
					.define("mobs_can_drop_packs", true);
			animalsCanDropPacks = builder
					.comment("Animals will drop packs on death.")
					.translation("config." + Reference.MODID + ".animalsCanDropPacks")
					.define("animals_can_drop_packs", false);
			playersCanDropPacks = builder
					.comment("Players will drop packs on death.")
					.translation("config." + Reference.MODID + ".playersCanDropPacks")
					.define("players_can_drop_packs", false);
			// Tiered card drop rates
			cardDropRateCommon = builder
					.comment("Chance out of X to drop common cards. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".cardDropRateCommon")
					.defineInRange("card_drop_rate_common", 0.0, 0.0, Double.MAX_VALUE);
			cardDropRateUncommon = builder
					.comment("Chance out of X to drop uncommon cards. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".cardDropRateUncommon")
					.defineInRange("card_drop_rate_uncommon", 0.0, 0.0, Double.MAX_VALUE);
			cardDropRateRare = builder
					.comment("Chance out of X to drop rare cards. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".cardDropRateRare")
					.defineInRange("card_drop_rate_rare", 0.0, 0.0, Double.MAX_VALUE);
			cardDropRateAncient = builder
					.comment("Chance out of X to drop ancient cards. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".cardDropRateAncient")
					.defineInRange("card_drop_rate_ancient", 0.0, 0.0, Double.MAX_VALUE);
			cardDropRateLegendary = builder
					.comment("Chance out of X to drop legendary cards. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".cardDropRateLegendary")
					.defineInRange("card_drop_rate_legendary", 0.0, 0.0, Double.MAX_VALUE);
			// Tiered pack drop rates
			packDropRateCommon = builder
					.comment("Chance out of X to drop common packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateCommon")
					.defineInRange("pack_drop_rate_common", 16.0, 0.0, Double.MAX_VALUE);
			packDropRateUncommon = builder
					.comment("Chance out of X to drop uncommon packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateUncommon")
					.defineInRange("pack_drop_rate_uncommon", 32.0, 0.0, Double.MAX_VALUE);
			packDropRateRare = builder
					.comment("Chance out of X to drop rare packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateRare")
					.defineInRange("pack_drop_rate_rare", 48.0, 0.0, Double.MAX_VALUE);
			packDropRateAncient = builder
					.comment("Chance out of X to drop ancient packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateAncient")
					.defineInRange("pack_drop_rate_ancient", 64.0, 0.0, Double.MAX_VALUE);
			packDropRateLegendary = builder
					.comment("Chance out of X to drop legendary packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateLegendary")
					.defineInRange("pack_drop_rate_legendary", 256.0, 0.0, Double.MAX_VALUE);
			// Non-tiered pack drop rates
			packDropRateStandard = builder
					.comment("Chance out of X to drop standard packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateStandard")
					.defineInRange("pack_drop_rate_standard", 40.0, 0.0, Double.MAX_VALUE);
			packDropRateEdition = builder
					.comment("Chance out of X to drop set-specific (edition) packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateEdition")
					.defineInRange("pack_drop_rate_edition", 40.0, 0.0, Double.MAX_VALUE);
			packDropRateCustom = builder
					.comment("Chance out of X to drop custom packs. Set to 0 to disable.")
					.translation("config." + Reference.MODID + ".packDropRateCustom")
					.defineInRange("pack_drop_rate_custom", 40.0, 0.0, Double.MAX_VALUE);
			// Other
			onlyOneDrop = builder
					.comment("If true, entities will not drop more than one MTC item at once.")
					.translation("config." + Reference.MODID + ".onlyOneDrop")
					.define("only_one_drop", false);
			// Boss drops
			enderDragonDrops = builder
					.comment("List of MTC drops from the Ender Dragon. Entries are of the form:"
							+ "\ndrop_item:float_amount"
							+ "\nPossible drop_item values are:"
							+ "\n[common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
							+ "\nfloat_amount is an either an integer representing the number of this item to drop, or a float like 4.7, where e.g. 70%, 5 will be dropped; otherwise 4 will."
							+ "\nThis list applies even if \"can_drop\" is false.")
					.translation("config." + Reference.MODID + ".enderDragonDrops")
					.define("ender_dragon_drops", new ArrayList<>(DropHandler.ENDER_DRAGON_DROPS_DEFAULT));
			bossDrops = builder
					.comment("List of MTC drops from the bosses that aren't the Ender Dragon. Entries are of the form:"
							+ "\ndrop_item:float_amount"
							+ "\nPossible drop_item values are:"
							+ "\n[common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
							+ "\nfloat_amount is an either an integer representing the number of this item to drop, or a float like 4.7, where e.g. 70%, 5 will be dropped; otherwise 4 will."
							+ "\nThis list applies even if \"can_drop\" is false.")
					.translation("config." + Reference.MODID + ".bossDrops")
					.define("boss_drops", new ArrayList<>(DropHandler.BOSS_DROPS_DEFAULT));
			builder.pop();

			// === Pack contents ===
			builder.push(CONFIG_CAT_PACK_CONTENTS);
			commonPackContents = builder
					.comment(cardPackDistributionDescription("Common", true))
					.translation("config." + Reference.MODID + ".commonPackContents")
					.define("common_pack_contents", new ArrayList<>(PackItemRarity.COMMON_PACK_CONTENT_DEFAULT));
			uncommonPackContents = builder
					.comment(cardPackDistributionDescription("Uncommon", true))
					.translation("config." + Reference.MODID + ".uncommonPackContents")
					.define("uncommon_pack_contents", new ArrayList<>(PackItemRarity.UNCOMMON_PACK_CONTENT_DEFAULT));
			rarePackContents = builder
					.comment(cardPackDistributionDescription("Rare", true))
					.translation("config." + Reference.MODID + ".rarePackContents")
					.define("rare_pack_contents", new ArrayList<>(PackItemRarity.RARE_PACK_CONTENT_DEFAULT));
			ancientPackContents = builder
					.comment(cardPackDistributionDescription("Ancient", true))
					.translation("config." + Reference.MODID + ".ancientPackContents")
					.define("ancient_pack_contents", new ArrayList<>(PackItemRarity.ANCIENT_PACK_CONTENT_DEFAULT));
			legendaryPackContents = builder
					.comment(cardPackDistributionDescription("Legendary", true))
					.translation("config." + Reference.MODID + ".legendaryPackContents")
					.define("legendary_pack_contents", new ArrayList<>(PackItemRarity.LEGENDARY_PACK_CONTENT_DEFAULT));
			standardPackContents = builder
					.comment(cardPackDistributionDescription("Standard", false))
					.translation("config." + Reference.MODID + ".standardPackContents")
					.define("standard_pack_contents", new ArrayList<>(PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT));
			editionPackContents = builder
					.comment(cardPackDistributionDescription("Edition", false))
					.translation("config." + Reference.MODID + ".editionPackContents")
					.define("edition_pack_contents", new ArrayList<>(PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT));
			builder.pop();

			// === Villager ===
			builder.push(CONFIG_CAT_VILLAGERS);
			cardMasterTrades = builder
					.comment("List of possible Card Master trades. Entries are of the form:"
							+ "\nsellitem|amount|buyitem1|amount|buyitem2|amount"
							+ "\n\"amount\" is either an integer, or a range like 1-3."
							+ "\nbuyitem2|amount is optional."
							+ "\nPossible sellitem and buyitem values are:"
							+ "\niron_ingot, gold_ingot, emerald, diamond, [common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
							+ "\nYou also append \"_random\" at the end of a _card entry (e.g. common_card_random) in order to generate a random card for sale (or requested).")
					.translation("config." + Reference.MODID + ".cardMasterTrades")
					.define("card_master_trades", new ArrayList<>(MineTradingCardVillagers.CARD_MASTER_TRADE_LIST_DEFAULT));
			cardTraderTrades = builder
					.comment("List of possible Card Trader trades. Entries are of the form:"
							+ "\nsellitem|amount|buyitem1|amount|buyitem2|amount"
							+ "\n\"amount\" is either an integer, or a range like 1-3."
							+ "\nbuyitem2|amount is optional."
							+ "\nPossible sellitem and buyitem values are:"
							+ "\niron_ingot, gold_ingot, emerald, diamond, [common/uncommon/rare/ancient/legendary/standard/edition/custom]_pack or [common/uncommon/rare/ancient/legendary]_card."
							+ "\nYou also append \"_random\" at the end of a _card entry (e.g. common_card_random) in order to generate a random card for sale (or requested).")
					.translation("config." + Reference.MODID + ".cardTraderTrades")
					.define("card_trader_trades", new ArrayList<>(MineTradingCardVillagers.CARD_TRADER_TRADE_LIST_DEFAULT));
			cardShopWeight = builder
					.comment("Weighting for selection when villages generate. Farms and wood huts are 3, church is 20.")
					.translation("config." + Reference.MODID + ".cardShopWeight")
					.defineInRange("card_shop_weight", 5, 0, 100);
			cardShopMaxNumber = builder
					.comment("Maximum number of card master shops that can spawn per village")
					.translation("config." + Reference.MODID + ".cardShopMaxNumber")
					.defineInRange("card_shop_max_number", 1, 0, 32);
			builder.pop();

			// === Logging ===
			builder.push(CONFIG_CAT_LOGS);
			devlogEnabled = builder
					.comment("Enable developer logs")
					.translation("config." + Reference.MODID + ".devlogEnabled")
					.define("devlog_enabled", false);
			builder.pop();

			// === Update Checker ===
			builder.push(CONFIG_CAT_UPDATES);
			enableUpdateChecker = builder
					.comment("Displays a client-side chat message on login if there's an update available.")
					.translation("config." + Reference.MODID + ".enableUpdateChecker")
					.define("enable_update_checker", true);
			builder.pop();
		}
	}
}
