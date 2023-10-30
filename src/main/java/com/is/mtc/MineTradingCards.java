package com.is.mtc;

import com.is.mtc.data_manager.DataLoader;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.handler.ConfigHandler;
import com.is.mtc.handler.DropHandler;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.init.MTCItems;
import com.is.mtc.packet.*;
import com.is.mtc.proxy.CommonProxy;
import com.is.mtc.root.CC_CreateCard;
import com.is.mtc.root.CC_ForceCreateCard;
import com.is.mtc.util.Reference;
import com.is.mtc.version.DevVersionWarning;
import com.is.mtc.version.VersionChecker;
import com.is.mtc.village.CardMasterHome;
import com.is.mtc.village.CardMasterHomeHandler;
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

import java.io.File;

import static com.is.mtc.init.MTCItems.displayerBlock;
import static com.is.mtc.init.MTCItems.monoDisplayerBlock;

@Mod(
		modid = Reference.MODID,
		name = Reference.NAME,
		version = Reference.MOD_VERSION,
		guiFactory = Reference.GUI_FACTORY
)
public class MineTradingCards {
	// The instance of the mod class that forge uses
	@Instance(Reference.MODID)
	public static MineTradingCards INSTANCE;

	// Whether the proxy is remote
	public static boolean PROXY_IS_REMOTE = false;
	public static String CONF_DIR = "";
	// Configurable data
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
	// Mod intercompatibility stuff
	public static boolean hasVillageNamesInstalled = false;
	// The proxy, either a combined client or a dedicated server
	@SidedProxy(clientSide = "com.is.mtc.proxy.ClientProxy", serverSide = "com.is.mtc.proxy.ServerProxy")
	public static CommonProxy PROXY;
	public static SimpleNetworkWrapper simpleNetworkWrapper; // The network wrapper for the mod
	// The creative tab that the mod uses
	public static CreativeTabs MODTAB = new CreativeTabs("tab_mtc") {
		public ItemStack createIcon() {
			return new ItemStack(MTCItems.packStandard);
		}
	};
	// The directories that MTC works with
	private static String DATA_DIR = "";

	public static String getDataDir() {
		return DATA_DIR;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		// Display
		event.getModMetadata().logoFile = "mtc_banner.png";

		// Gets the config and reads the cards, and runs the preinitialisation from the proxy
		DATA_DIR = event.getModConfigurationDirectory().getParentFile().getAbsolutePath().replace('\\', '/') + "/resourcepacks/";
		CONF_DIR = event.getModConfigurationDirectory().getAbsolutePath().replace('\\', '/') + '/';
		MTCItems.init();

		// Version check monitor
		if (Reference.MOD_VERSION.contains("DEV") || Reference.MOD_VERSION.equals("@VERSION@")) {
			MinecraftForge.EVENT_BUS.register(DevVersionWarning.instance);
		} else if (ENABLE_UPDATE_CHECKER) {
			MinecraftForge.EVENT_BUS.register(VersionChecker.instance);
		}

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
		ConfigHandler.config = new Configuration(new File(CONF_DIR, "Mine Trading Cards.cfg"), Reference.CONFIG_VERSION, false);
		ConfigHandler.config.load();
		ConfigHandler.saveConfig();
	}
}
