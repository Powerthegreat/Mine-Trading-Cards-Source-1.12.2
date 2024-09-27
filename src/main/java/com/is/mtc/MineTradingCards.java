package com.is.mtc;

import com.is.mtc.data_manager.DataLoader;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.handler.ConfigHandler;
import com.is.mtc.init.MTCBlocks;
import com.is.mtc.init.MTCContainers;
import com.is.mtc.init.MTCItems;
import com.is.mtc.init.MTCTileEntities;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.util.Reference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;

@Mod(Reference.MODID)
public class MineTradingCards {
	// Mod intercompatibility stuff
	//public static boolean hasVillageNamesInstalled = false;
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	// The directories that MTC works with
	private static final String DATA_DIR = FMLPaths.CONFIGDIR.get().getParent().toString().replace('\\', '/') + "/resourcepacks/";
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
	public static ItemGroup ITEMGROUP_MTC = new ItemGroup(Reference.MODID + ".tab_mtc") {
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(MTCItems.packStandard.get());
		}
	};

	public MineTradingCards() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC, FMLPaths.CONFIGDIR.get() + "/MineTradingCards.toml");

		MTCItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		MTCBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		MTCTileEntities.TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		MTCContainers.CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

		int packetId = 0;
		Function<PacketBuffer, MTCMessage> function;
		NETWORK_CHANNEL.registerMessage(packetId++, MTCMessage.class, MTCMessage::toBytes, MTCMessage::fromBytes, MTCMessage::onMessage);

		Databank.setup();
		DataLoader.readAndLoad();
	}

	public static String getDataDir() {
		return DATA_DIR;
	}

	/*@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MapGenStructureIO.registerStructureComponent(CardMasterHome.class, "Mtc_Cm_House"); // Register the house to the generator with a typed id
		// Registers the Card Master villager's trades, and the creation handler for its home
		VillagerRegistry.instance().registerVillageCreationHandler(new CardMasterHomeHandler());
	}*/
}
