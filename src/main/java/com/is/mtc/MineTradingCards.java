package com.is.mtc;

import static com.is.mtc.init.MTCItems.displayerBlock;
import static com.is.mtc.init.MTCItems.monoDisplayerBlock;

import java.io.File;

import com.is.mtc.data_manager.DataLoader;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.handler.DropHandler;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.init.MTCItems;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.packet.MTCMessageHandler;
import com.is.mtc.packet.MTCMessageRequestUpdateDisplayer;
import com.is.mtc.packet.MTCMessageRequestUpdateDisplayerHandler;
import com.is.mtc.packet.MTCMessageUpdateDisplayer;
import com.is.mtc.packet.MTCMessageUpdateDisplayerHandler;
import com.is.mtc.proxy.CommonProxy;
import com.is.mtc.root.CC_CreateCard;
import com.is.mtc.root.CC_ForceCreateCard;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Functions;
import com.is.mtc.util.Reference;
import com.is.mtc.version.DevVersionWarning;
import com.is.mtc.version.VersionChecker;
import com.is.mtc.village.CardMasterHome;
import com.is.mtc.village.CardMasterHomeHandler;
import com.is.mtc.village.MineTradingCardVillagers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MODID, version = Reference.MOD_VERSION, name = Reference.NAME)
public class MineTradingCards {
	// The instance of the mod class that forge uses
	@Instance(Reference.MODID)
	public static MineTradingCards INSTANCE;

	// Whether the proxy is remote
	public static boolean PROXY_IS_REMOTE = false;
	
	// The directories that MTC works with
	private static String DATA_DIR = "";
	private static String CONF_DIR = "";

	// Configuration stuff
	public static final String CONFIG_CAT_COLORS = "colors";
	public static final String CONFIG_CAT_DROPS = "drops";
	public static final String CONFIG_CAT_LOGS = "logs";
	public static final String CONFIG_CAT_RECIPES = "recipes";
	public static final String CONFIG_CAT_UPDATES = "updates";
	public static final String CONFIG_CAT_VILLAGERS = "villagers";
		
	public static int CARD_COLOR_COMMON = Reference.COLOR_GREEN;
	public static int CARD_COLOR_UNCOMMON = Reference.COLOR_GOLD;
	public static int CARD_COLOR_RARE = Reference.COLOR_RED;
	public static int CARD_COLOR_ANCIENT = Reference.COLOR_AQUA;
	public static int CARD_COLOR_LEGENDARY = Reference.COLOR_LIGHT_PURPLE;
	public static String CARD_TOOLTIP_COLOR_COMMON = "green";
	public static String CARD_TOOLTIP_COLOR_UNCOMMON = "gold";
	public static String CARD_TOOLTIP_COLOR_RARE = "red";
	public static String CARD_TOOLTIP_COLOR_ANCIENT = "aqua";
	public static String CARD_TOOLTIP_COLOR_LEGENDARY = "light_purple";
	public static int PACK_COLOR_COMMON = Reference.COLOR_GREEN;
	public static int PACK_COLOR_UNCOMMON = Reference.COLOR_GOLD;
	public static int PACK_COLOR_RARE = Reference.COLOR_RED;
	public static int PACK_COLOR_ANCIENT = Reference.COLOR_AQUA;
	public static int PACK_COLOR_LEGENDARY = Reference.COLOR_LIGHT_PURPLE;
	public static int PACK_COLOR_STANDARD = Reference.COLOR_BLUE;
	public static boolean ENABLE_UPDATE_CHECKER = true;
	
	public static final String COLOR_ITEM_DESCRIPTION_1 = "Color for ";
	public static final String COLOR_ITEM_DESCRIPTION_2 = "Entered as a decimal integer, or as a hexadecimal by putting # in front.";
	public static final String COLOR_TOOLTIP_1 = "Tooltip color for ";
	public static final String COLOR_TOOLTIP_2 = " cards, using \"friendly\" Minecraft color name";
	
	// Mod intercompatibility stuff
	public static boolean hasVillageNamesInstalled = false;
	
	// The proxy, either a combined client or a dedicated server
	@SidedProxy(clientSide = "com.is.mtc.proxy.ClientProxy", serverSide = "com.is.mtc.proxy.ServerProxy")
	public static CommonProxy PROXY;
	public static SimpleNetworkWrapper simpleNetworkWrapper; // The network wrapper for the mod

	// The creative tab that the mod uses
	public static CreativeTabs MODTAB = new CreativeTabs("tab_mtc") {
		public ItemStack getTabIconItem() {
			return new ItemStack(MTCItems.packStandard);
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Gets the config and reads the cards, and runs the preinitialisation from the proxy
		DATA_DIR = event.getModConfigurationDirectory().getParentFile().getAbsolutePath().replace('\\', '/') + "/mtc/";
		CONF_DIR = event.getModConfigurationDirectory().getAbsolutePath().replace('\\', '/') + '/';
		MTCItems.init();

        // Version check monitor
        if (Reference.MOD_VERSION.contains("DEV") || Reference.MOD_VERSION.equals("@VERSION@")) {MinecraftForge.EVENT_BUS.register(DevVersionWarning.instance);}
        else if (ENABLE_UPDATE_CHECKER) {MinecraftForge.EVENT_BUS.register(VersionChecker.instance);}
        
		PROXY.preInit(event);
		readConfig(event);

		Databank.setup();
		DataLoader.readAndLoad();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Runs the initialisation from the proxy, then defines the items and blocks
		PROXY.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// Runs the postinitialisation from the proxy, then registers the items and blocks
		PROXY.postInit(event);

		// Sets up the network wrapper
		simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
		simpleNetworkWrapper.registerMessage(MTCMessageHandler.class, MTCMessage.class, 0, Side.SERVER);
		simpleNetworkWrapper.registerMessage(MTCMessageUpdateDisplayerHandler.class, MTCMessageUpdateDisplayer.class, 1, Side.CLIENT);
		simpleNetworkWrapper.registerMessage(MTCMessageRequestUpdateDisplayerHandler.class, MTCMessageRequestUpdateDisplayer.class, 2, Side.SERVER);
		
		// Sets up the gui and drop handlers
		MinecraftForge.EVENT_BUS.register(new DropHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
		
		// Registers tile entities
		GameRegistry.registerTileEntity(DisplayerBlockTileEntity.class, new ResourceLocation(displayerBlock.getRegistryName().toString()));
		GameRegistry.registerTileEntity(MonoDisplayerBlockTileEntity.class, new ResourceLocation(monoDisplayerBlock.getRegistryName().toString()));

		MapGenStructureIO.registerStructureComponent(CardMasterHome.class, "Mtc_Cm_House"); // Register the house to the generator with a typed id
		// Registers the Card Master villager's trades, and the creation handler for its home
		VillagerRegistry.instance().registerVillageCreationHandler(new CardMasterHomeHandler());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		// Registers the cards on a server
		event.registerServerCommand(new CC_CreateCard());
		event.registerServerCommand(new CC_ForceCreateCard());
	}

	private void readConfig(FMLPreInitializationEvent event) {
		// Loads from the configuration file
		Configuration config = new Configuration(new File(CONF_DIR, "Mine Trading Cards.cfg"), Reference.CONFIG_VERSION, false);
		config.load();

		// === Colors ===
		// Cards
		CARD_COLOR_COMMON = Functions.parseColorInteger(config.getString("card_color_common", CONFIG_CAT_COLORS, "#55ff55", COLOR_ITEM_DESCRIPTION_1+"common cards. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GREEN);
		CARD_COLOR_UNCOMMON = Functions.parseColorInteger(config.getString("card_color_uncommon", CONFIG_CAT_COLORS, "#ffaa00", COLOR_ITEM_DESCRIPTION_1+"uncommon cards. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GOLD);
		CARD_COLOR_RARE = Functions.parseColorInteger(config.getString("card_color_rare", CONFIG_CAT_COLORS, "#ff5555", COLOR_ITEM_DESCRIPTION_1+"rare cards. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_RED);
		CARD_COLOR_ANCIENT = Functions.parseColorInteger(config.getString("card_color_ancient", CONFIG_CAT_COLORS, "#55ffff", COLOR_ITEM_DESCRIPTION_1+"ancient cards. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_AQUA);
		CARD_COLOR_LEGENDARY = Functions.parseColorInteger(config.getString("card_color_legendary", CONFIG_CAT_COLORS, "#ff55ff", COLOR_ITEM_DESCRIPTION_1+"legendary cards. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_LIGHT_PURPLE);
		// Tooltips
		CARD_TOOLTIP_COLOR_COMMON = config.getString("card_tooltip_color_common", CONFIG_CAT_COLORS, "green", COLOR_TOOLTIP_1+"common"+COLOR_TOOLTIP_2);
		CARD_TOOLTIP_COLOR_UNCOMMON = config.getString("card_tooltip_color_uncommon", CONFIG_CAT_COLORS, "gold", COLOR_TOOLTIP_1+"uncommon"+COLOR_TOOLTIP_2);
		CARD_TOOLTIP_COLOR_RARE = config.getString("card_tooltip_color_rare", CONFIG_CAT_COLORS, "red", COLOR_TOOLTIP_1+"rare"+COLOR_TOOLTIP_2);
		CARD_TOOLTIP_COLOR_ANCIENT = config.getString("card_tooltip_color_ancient", CONFIG_CAT_COLORS, "aqua", COLOR_TOOLTIP_1+"ancient"+COLOR_TOOLTIP_2);
		CARD_TOOLTIP_COLOR_LEGENDARY = config.getString("card_tooltip_color_legendary", CONFIG_CAT_COLORS, "light_purple", COLOR_TOOLTIP_1+"legendary"+COLOR_TOOLTIP_2);
		// Packs
		PACK_COLOR_COMMON = Functions.parseColorInteger(config.getString("pack_color_common", CONFIG_CAT_COLORS, "#55ff55", COLOR_ITEM_DESCRIPTION_1+"common packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GREEN);
		PACK_COLOR_UNCOMMON = Functions.parseColorInteger(config.getString("pack_color_uncommon", CONFIG_CAT_COLORS, "#ffaa00", COLOR_ITEM_DESCRIPTION_1+"uncommon packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_GOLD);
		PACK_COLOR_RARE = Functions.parseColorInteger(config.getString("pack_color_rare", CONFIG_CAT_COLORS, "#ff5555", COLOR_ITEM_DESCRIPTION_1+"rare packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_RED);
		PACK_COLOR_ANCIENT = Functions.parseColorInteger(config.getString("pack_color_ancient", CONFIG_CAT_COLORS, "#55ffff", COLOR_ITEM_DESCRIPTION_1+"ancient packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_AQUA);
		PACK_COLOR_LEGENDARY = Functions.parseColorInteger(config.getString("pack_color_legendary", CONFIG_CAT_COLORS, "#ff55ff", COLOR_ITEM_DESCRIPTION_1+"legendary packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_LIGHT_PURPLE);
		PACK_COLOR_STANDARD = Functions.parseColorInteger(config.getString("pack_color_standard", CONFIG_CAT_COLORS, "#5555ff", COLOR_ITEM_DESCRIPTION_1+"standard packs. "+COLOR_ITEM_DESCRIPTION_2).trim(), Reference.COLOR_BLUE);
		
		// === Drops === 
		// Drops toggle
		DropHandler.CAN_DROP_CARDS_MOB = config.getBoolean("mobs_can_drop_cards", CONFIG_CAT_DROPS, true, "Mobs will drop cards on death.");
		DropHandler.CAN_DROP_CARDS_ANIMAL = config.getBoolean("animals_can_drop_cards", CONFIG_CAT_DROPS, false, "Animals will drop cards on death.");
		DropHandler.CAN_DROP_CARDS_PLAYER = config.getBoolean("players_can_drop_cards", CONFIG_CAT_DROPS, false, "Players will drop cards on death.");
		DropHandler.CAN_DROP_PACKS_MOB = config.getBoolean("mobs_can_drop_packs", CONFIG_CAT_DROPS, true, "Mobs will drop packs on death.");
		DropHandler.CAN_DROP_PACKS_ANIMAL = config.getBoolean("animals_can_drop_packs", CONFIG_CAT_DROPS, false, "Animals will drop packs on death.");
		DropHandler.CAN_DROP_PACKS_PLAYER = config.getBoolean("players_can_drop_packs", CONFIG_CAT_DROPS, false, "Players will drop packs on death.");
		// Tiered card drop rates
		DropHandler.CARD_DROP_RATE_COM = config.getFloat("card_drop_rate_common", CONFIG_CAT_DROPS, 16, 0, Float.MAX_VALUE, "Chance out of X to drop common cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_UNC = config.getFloat("card_drop_rate_uncommon", CONFIG_CAT_DROPS, 32, 0, Float.MAX_VALUE, "Chance out of X to drop uncommon cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_RAR = config.getFloat("card_drop_rate_rare", CONFIG_CAT_DROPS, 48, 0, Float.MAX_VALUE, "Chance out of X to drop rare cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_ANC = config.getFloat("card_drop_rate_ancient", CONFIG_CAT_DROPS, 64, 0, Float.MAX_VALUE, "Chance out of X to drop ancient cards. Set to 0 to disable.");
		DropHandler.CARD_DROP_RATE_LEG = config.getFloat("card_drop_rate_legendary", CONFIG_CAT_DROPS, 256, 0, Float.MAX_VALUE, "Chance out of X to drop legendary cards. Set to 0 to disable.");
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
		
		// === Logging ===
		Logs.ENABLE_DEV_LOGS = config.getBoolean("devlog_enabled", CONFIG_CAT_LOGS, false, "Enable developer logs");
		
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
		
		// === Update Checker ===
		ENABLE_UPDATE_CHECKER = config.getBoolean("enable_update_checker", CONFIG_CAT_UPDATES, true, "Displays a client-side chat message on login if there's an update available.");
		
		
		config.save();
	}

	public static String getDataDir() {
		return DATA_DIR;
	}
}
