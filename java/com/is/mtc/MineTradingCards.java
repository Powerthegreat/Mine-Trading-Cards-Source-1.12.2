package com.is.mtc;

import java.io.File;

import com.is.mtc.root.CC_CreateCard;
import com.is.mtc.root.CC_ForceCreateCard;
import com.is.mtc.root.Logs;
import com.is.mtc.init.MTCItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.is.mtc.data_manager.DataLoader;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.handler.DropHandler;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.packet.MTCMessageHandler;
import com.is.mtc.proxy.CommonProxy;
import com.is.mtc.village.CardMasterHome;
import com.is.mtc.village.CardMasterHomeHandler;

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

@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME)
public class MineTradingCards {
	// The instance of the mod class that forge uses
	@Instance(Reference.MODID)
	public static MineTradingCards INSTANCE;

	// Whether the proxy is remote
	public static boolean PROXY_IS_REMOTE = false;

	// The directories that MTC works with
	private static String DATA_DIR = "";
	private static String CONF_DIR = "";

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
	//-

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Gets the config and reads the cards, and runs the preinitialisation from the proxy
		DATA_DIR = event.getModConfigurationDirectory().getParentFile().getAbsolutePath().replace('\\', '/') + "/mtc/";
		CONF_DIR = event.getModConfigurationDirectory().getAbsolutePath().replace('\\', '/') + '/';
		MTCItems.init();

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

		// Sets up the gui and drop handlers
		MinecraftForge.EVENT_BUS.register(new DropHandler());
		//NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

		// Registers tile entities
		GameRegistry.registerTileEntity(DisplayerBlockTileEntity.class, new ResourceLocation("is_mtc:block_displayer"));
		GameRegistry.registerTileEntity(MonoDisplayerBlockTileEntity.class, new ResourceLocation("is_mtc:block_monodisplayer"));

		MapGenStructureIO.registerStructureComponent(CardMasterHome.class, "Mtc_Cm_House"); // Register the house to the generator with a typed id
		// Registers the Card Master villager's trades, and the creation handler for its home
		//VillagerRegistry.instance().registerVillageTradeHandler(VillageHandler.TRADER_ID, new VillageHandler());
		VillagerRegistry.instance().registerVillageCreationHandler(new CardMasterHomeHandler());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		// Registers the cards on a server
		event.registerServerCommand(new CC_CreateCard());
		event.registerServerCommand(new CC_ForceCreateCard());
	}

	//-

	private void readConfig(FMLPreInitializationEvent event) {
		// Loads from the configuration file
		Configuration config = new Configuration(new File(CONF_DIR, "Mine Trading Cards.cfg"), "1v", false);
		config.load();

		Logs.ENABLE_DEV_LOGS = config.getBoolean("devlog_enabled", "logs", false, "Enable developper logs");

		DropHandler.CAN_DROP_MOB = config.getBoolean("mobs_can_drop", "drops", true, "Can mobs drop packs on death");
		DropHandler.CAN_DROP_ANIMAL = config.getBoolean("animals_can_drop", "drops", false, "Can animals drop packs on death");
		DropHandler.CAN_DROP_PLAYER = config.getBoolean("players_can_drop", "drops", false, "Can players drop packs on death");

		DropHandler.DROP_RATE_COM = config.getInt("pack_drop_rate_common", "drops", 16, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_UNC = config.getInt("pack_drop_rate_uncommon", "drops", 32, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_RAR = config.getInt("pack_drop_rate_rare", "drops", 48, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_ANC = config.getInt("pack_drop_rate_ancient", "drops", 64, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_LEG = config.getInt("pack_drop_rate_legendary", "drops", 256, 0, Integer.MAX_VALUE, "1 chance out of X to drop");

		DropHandler.DROP_RATE_STD = config.getInt("pack_drop_rate_standard", "drops", 40, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_EDT = config.getInt("pack_drop_rate_edition", "drops", 40, 0, Integer.MAX_VALUE, "1 chance out of X to drop");

		config.save();
	}

	public static String getDataDir() {
		return DATA_DIR;
	}
}
