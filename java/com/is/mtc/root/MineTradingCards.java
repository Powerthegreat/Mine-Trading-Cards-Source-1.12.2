package com.is.mtc.root;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.is.mtc.binder.BinderItem;
import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.DataLoader;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlock;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlock;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.handler.DropHandler;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.pack.PackItemBase;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.packet.MTCMessageHandler;
import com.is.mtc.proxy.CommonProxy;
import com.is.mtc.village.CardMasterHome;
import com.is.mtc.village.CardMasterHomeHandler;
import com.is.mtc.village.VillageHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = MineTradingCards.MODID, version = MineTradingCards.VERSION, name = MineTradingCards.NAME)
public class MineTradingCards {

	public static final String MODID = "is_mtc";
	public static final String VERSION = "2.1";
	public static final String NAME = "Mine Trading Cards";

	@Instance(MineTradingCards.MODID)
	public static MineTradingCards INSTANCE;

	public static boolean PROXY_IS_REMOTE = false;

	public static CardItem cc, cu, cr, ca, cl;
	public static PackItemBase pc, pu, pr, pa, pl, ps, pe; // com, unc rar anc leg standard edition

	public static BinderItem bi;
	public static DisplayerBlock db;
	public static MonoDisplayerBlock mdb;

	private static String DATA_DIR = "";
	private static String CONF_DIR = "";

	@SidedProxy(clientSide = "com.is.mtc.proxy.ClientProxy", serverSide = "com.is.mtc.proxy.ServerProxy")
	public static CommonProxy PROXY;
	public static SimpleNetworkWrapper snw;

	public static CreativeTabs MODTAB = new CreativeTabs("tab_mtc") { @Override public Item getTabIconItem() { return MineTradingCards.ps; } };

	//-

	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		DATA_DIR = e.getModConfigurationDirectory().getParentFile().getAbsolutePath().replace('\\', '/') + "/mtc/";
		CONF_DIR = e.getModConfigurationDirectory().getAbsolutePath().replace('\\', '/') + '/';

		PROXY.preInit(e);
		readConfig(e);

		Databank.setup();
		DataLoader.readAndLoad();
	}

	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		PROXY.init(e);

		cc = new CardItem(Rarity.COMMON);
		cu = new CardItem(Rarity.UNCOMMON);
		cr = new CardItem(Rarity.RARE);
		ca = new CardItem(Rarity.ANCIENT);
		cl = new CardItem(Rarity.LEGENDARY);

		pc = new PackItemRarity(Rarity.COMMON);
		pu = new PackItemRarity(Rarity.UNCOMMON);
		pr = new PackItemRarity(Rarity.RARE);
		pa = new PackItemRarity(Rarity.ANCIENT);
		pl = new PackItemRarity(Rarity.LEGENDARY);

		ps = new PackItemStandard();
		pe = new PackItemEdition();

		bi = new BinderItem();
		db = new DisplayerBlock();
		mdb = new MonoDisplayerBlock();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		PROXY.postInit(e);

		Injector.registerItem(cc);
		Injector.registerItem(cu);
		Injector.registerItem(cr);
		Injector.registerItem(ca);
		Injector.registerItem(cl);

		Injector.registerItem(pc);
		Injector.registerItem(pu);
		Injector.registerItem(pr);
		Injector.registerItem(pa);
		Injector.registerItem(pl);

		Injector.registerItem(ps);
		Injector.registerItem(pe);

		Injector.registerItem(bi);
		Injector.registerBlock(db);
		Injector.registerBlock(mdb);

		snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		snw.registerMessage(MTCMessageHandler.class, MTCMessage.class, 0, Side.SERVER);

		MinecraftForge.EVENT_BUS.register(new DropHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

		GameRegistry.registerTileEntity(DisplayerBlockTileEntity.class, "tile_entity_displayer");
		GameRegistry.registerTileEntity(MonoDisplayerBlockTileEntity.class, "tile_entity_monodisplayer");

		GameRegistry.addRecipe(new ItemStack(db), new Object[] { "IGI", "GgG", "IGI",
			'I', Items.iron_ingot, 'G', Blocks.glass, 'g', Blocks.glowstone });

		GameRegistry.addRecipe(new ItemStack(mdb, 4), new Object[] { "IWI", "WgW", "IGI",
			'I', Items.iron_ingot, 'G', Blocks.glass, 'g', Blocks.glowstone, 'W', Blocks.planks });

		GameRegistry.addShapelessRecipe(new ItemStack(bi), new Object[] {
			Items.book, cc });

		MapGenStructureIO.func_143031_a(CardMasterHome.class, "Mtc_Cm_Jouse"); // Register the house to the generator with a typed id
		VillagerRegistry.instance().registerVillageTradeHandler(VillageHandler.TRADER_ID, new VillageHandler());
		VillagerRegistry.instance().registerVillageCreationHandler(new CardMasterHomeHandler());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CC_CreateCard());
		event.registerServerCommand(new CC_ForceCreateCard());
	}

	//-

	private void readConfig(FMLPreInitializationEvent e)
	{
		Configuration conf = new Configuration(new File(CONF_DIR, "Mine Trading Cards.cfg"), "1v", false);
		conf.load();

		Logs.ENABLE_DEV_LOGS = conf.getBoolean("devlog_enabled", "logs", false, "Enable developper logs");

		DropHandler.CAN_DROP_MOB = conf.getBoolean("mobs_can_drop", "drops", true, "Can mobs drop packs on death");
		DropHandler.CAN_DROP_ANIMAL = conf.getBoolean("animals_can_drop", "drops", false, "Can animals drop packs on death");
		DropHandler.CAN_DROP_PLAYER = conf.getBoolean("players_can_drop", "drops", false, "Can players drop packs on death");

		DropHandler.DROP_RATE_COM = conf.getInt("pack_drop_rate_common", "drops", 16, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_UNC = conf.getInt("pack_drop_rate_uncommon", "drops", 32, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_RAR = conf.getInt("pack_drop_rate_rare", "drops", 48, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_ANC = conf.getInt("pack_drop_rate_ancient", "drops", 64, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_LEG = conf.getInt("pack_drop_rate_legendary", "drops", 256, 0, Integer.MAX_VALUE, "1 chance out of X to drop");

		DropHandler.DROP_RATE_STD = conf.getInt("pack_drop_rate_standard", "drops", 40, 0, Integer.MAX_VALUE, "1 chance out of X to drop");
		DropHandler.DROP_RATE_EDT = conf.getInt("pack_drop_rate_edition", "drops", 40, 0, Integer.MAX_VALUE, "1 chance out of X to drop");

		conf.save();
	}

	public static String getDataDir()
	{
		return DATA_DIR;
	}
}
