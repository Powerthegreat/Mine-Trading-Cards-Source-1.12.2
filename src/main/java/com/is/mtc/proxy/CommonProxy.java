package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Reference;
import com.is.mtc.village.MineTradingCardVillagers;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public static boolean IS_REMOTE = false;

	public void preInit(FMLPreInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(MineTradingCards.INSTANCE, new GuiHandler());
	}

	public void init(FMLInitializationEvent event) {
		MineTradingCardVillagers.registerCareers();

		// Mod intercompat stuff
		if (Loader.isModLoaded(Reference.VILLAGE_NAMES_MODID)) {
			MineTradingCards.hasVillageNamesInstalled = true;
		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		Logs.devLog("Proxy: Is remote ?: " + IS_REMOTE);
	}
}
